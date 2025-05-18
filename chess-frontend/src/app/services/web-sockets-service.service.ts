import { Injectable } from '@angular/core';
import SockJS from 'sockjs-client';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { share } from 'rxjs/operators';

import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WebSocketsServiceService {
  private stompClient: Client;
  private jwtToken = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXNobzMiLCJpYXQiOjE3NDY4OTQ0OTYsImV4cCI6MTc2MjQ0NjQ5Nn0.7JsEnIkUFuOAzJcwYhUsDpRcraM2LwttCPVKIeuH2O-PgZ-o2UMgtweQFwcKk6mx3c2McywP-OsqGA7wV2Dk7Q'; // your full token
  private roomObservables: { [roomId: string]: Observable<any> } = {};
  constructor() { 
    this.stompClient = new Client({
          webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
          connectHeaders: this.getHeaders(),
          debug: (msg: string) => console.log('[STOMP]', msg),
          onConnect: () => console.log('STOMP connected'),
          onStompError: (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
          }
        });
  }

  private getHeaders() {
    return {
      Authorization: `Bearer ${this.jwtToken}`,
      'heart-beat': '10000,10000'
    };
  }

   public joinRoom(roomId: string): Observable<any> {
    if (!this.roomObservables[roomId]) {
      this.roomObservables[roomId] = this.createRoomObservable(roomId).pipe(share());
    }
    return this.roomObservables[roomId];
  }

 private createRoomObservable(roomId: string): Observable<any> {
    return new Observable(observer => {
      const subscription = this.stompClient.subscribe(
        `/room/game.${roomId}`,
        (message: IMessage) => {
          try {
            console.log(message)
            const msg = JSON.parse(message.body);
            observer.next(msg);
          } catch (e) {
            observer.error('Message parsing failed');
          }
        },
        this.getHeaders()
      );

      this.initialConnect(roomId);

      return () => {
        subscription.unsubscribe();
        delete this.roomObservables[roomId];
      };
    });
  }


  public initialConnect(roomId: string): void {
    this.stompClient.publish({
      destination: '/app/game.initialConnect',
      headers: this.getHeaders(),
      body: JSON.stringify({ roomId })
    });
  }

  public sendMessage(move: string, roomId: string,isBotMode:boolean): void {
    this.stompClient.publish({
      destination: isBotMode
        ? '/app/game.makeMoveToBot'
        : '/app/game.makeMoveToPlayer',
      headers: this.getHeaders(),
      body: JSON.stringify({ move, roomId })
    });
  }
  public activate(){
    this.stompClient.activate();

  }
}
