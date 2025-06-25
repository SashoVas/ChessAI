import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { HttpClient } from '@angular/common/http';
import { Game } from '../models/game';
import { Room } from '../models/room';
import { CreateRoomModel } from '../models/createRoomModel';

@Injectable({
  providedIn: 'root'
})
export class RoomServiceService {

  constructor(private authService:AuthService,private httpClient:HttpClient) { }

  public createRoom(room:CreateRoomModel){
      return this.httpClient.post<Game>('http://localhost:8080/games',
        room,
        {headers: {
          'Authorization': `Bearer ${this.authService.getToken()}`,
        }});
      
  }

  public joinRoom(roomId:string){
      return this.httpClient.post<Game>('http://localhost:8080/games/'+roomId,
        {  },
        {headers: {
          'Authorization': `Bearer ${this.authService.getToken()}`,
        }});
  }

  public getFreeRooms(){
    return this.httpClient.get<Room[]>('http://localhost:8080/games?free=true',
        {headers: {
          'Authorization': `Bearer ${this.authService.getToken()}`,
        }});
  }
}
