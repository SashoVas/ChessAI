import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

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
  selectedGameTime = 'MULTIPLAYER';
  gameColors=["RANDOM","WHITE","BLACK"]
  selectedColor = 'RANDOM';

  startCustomGame(){
    this.isOpen=!this.isOpen;
  }
  onCreate() {

  }

  onClose() {
    this.isOpen=!this.isOpen;

  }
}
