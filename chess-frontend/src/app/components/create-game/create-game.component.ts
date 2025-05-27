import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RoomServiceService } from '../../services/room-service.service';
import { Router } from '@angular/router';
import { Game } from '../../models/game';

@Component({
  selector: 'app-create-game',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './create-game.component.html',
  styleUrl: './create-game.component.css'
})
export class CreateGameComponent {

  timeMinutes=5;
  timeSeconds = 0;
  isOpen=false;

  gameModes = ['MULTIPLAYER','BOT'];
  selectedGameType = 'MULTIPLAYER';
  gameColors=["RANDOM","WHITE","BLACK"]
  selectedColor = 'RANDOM';

  constructor(
    private roomService:RoomServiceService,
    private router: Router
  ){}

  startCustomGame(){
    this.isOpen=!this.isOpen;
  }
  onCreate() {
    this.roomService.createRoom(this.selectedGameType == 'BOT')
      .subscribe((json:Game) => {
        this.router.navigate(['/game/' + json.gameId]);
      });
  }

  onClose() {
    this.isOpen=!this.isOpen;

  }
}
