import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { CreateGameComponent } from '../create-game/create-game.component';
import { RoomServiceService } from '../../services/room-service.service';
import { Router } from '@angular/router';
import { Game } from '../../models/game';
import { TimeControl } from '../../models/timeControl';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [CommonModule,CreateGameComponent],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css'
})
export class HomePageComponent {

  timeControls: TimeControl[] = [
    { name: 'Bullet', time: 1 },
    { name: 'Blitz', time: 3 },
    { name: 'Rapid', time: 10 },
    { name: 'Classical', time: 30 },

  ];

  constructor(    
    private roomService:RoomServiceService,
    private router: Router){

  }
  hoverEffect(timeControl: TimeControl) {
   }

  startAIGame() {
   }

  startCustomGame() {
   }

  createGameFromPredefinedTimeControls(timeControl:TimeControl){
    console.log('hi')
    this.roomService.createRoom({    
      gameType: 'MULTIPLAYER',
      gameTimeSeconds:timeControl.time * 60})
      .subscribe((json:Game) => {
        this.router.navigate(['/game/' + json.gameId]);
      });
  }
}
