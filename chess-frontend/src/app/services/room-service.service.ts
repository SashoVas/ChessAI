import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RoomServiceService {

  constructor(private authService:AuthService,private httpClient:HttpClient) { }

  public createRoom(isBotMode:boolean){
      return fetch('http://localhost:8080/createGame', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${this.authService.getToken()}`
        },
        body: JSON.stringify({ gameType:isBotMode? 'BOT':'MULTIPLAYER', gameTimeSeconds: 60 })
      }).then(res => res.json())
  }
  public joinRoom(roomId:string){
    return fetch('http://localhost:8080/game/'+roomId, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${this.authService.getToken()}`
        },
        body: JSON.stringify({ })
      }).then(res => res.json())
    //return this.httpClient.patch('http://localhost:8080/game/'+roomId,{
    //  headers: {
    //      'Content-Type': 'application/json',
    //      Authorization: `Bearer ${this.authService.getToken()}`
    //    },
    //})
  }
}
