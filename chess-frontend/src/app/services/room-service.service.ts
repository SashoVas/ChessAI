import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoomServiceService {

  constructor(private authService:AuthService) { }

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
  public joinRoom(){
    
  }
}
