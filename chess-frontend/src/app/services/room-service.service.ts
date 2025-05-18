import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class RoomServiceService {
  private jwtToken = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXNobzMiLCJpYXQiOjE3NDY4OTQ0OTYsImV4cCI6MTc2MjQ0NjQ5Nn0.7JsEnIkUFuOAzJcwYhUsDpRcraM2LwttCPVKIeuH2O-PgZ-o2UMgtweQFwcKk6mx3c2McywP-OsqGA7wV2Dk7Q'; // your full token

  constructor() { }

  public createRoom(){
      return fetch('http://localhost:8080/createGame', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${this.jwtToken}`
        },
        body: JSON.stringify({ gameType: 'BOT', gameTimeSeconds: 60 })
      }).then(res => res.json())
  }
}
