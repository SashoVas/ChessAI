import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RoomServiceService } from '../../services/room-service.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Game } from '../../models/game';

@Component({
  selector: 'app-create-game',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './create-game.component.html',
  styleUrl: './create-game.component.css'
})
export class CreateGameComponent {

  @Input() selectedGameType:string='NONE' ;
  @Input() buttonName:string='Create Game';
  @Input() bgColor:string='bg-blue';
  @Input() showFormDirectly:boolean=false;

  gameTimeSeconds = 300; // Default 5 minutes
  manualTimeInput = '5'; // Default 5 minutes
  isOpen = false;

  gameModes = ['MULTIPLAYER','BOT'];
  gameColors = ["RANDOM","WHITE","BLACK"];
  selectedColor = 'RANDOM';

  constructor(
    private roomService:RoomServiceService,
    private router: Router,
    private route: ActivatedRoute
  ){}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      if (params['showForm'] === 'true') {
        this.showFormDirectly = true;
        this.isOpen = true;
      }
    });
  }

  startCustomGame(){
    this.isOpen=!this.isOpen;
  }

  formatTime(): string {
    const minutes = Math.floor(this.gameTimeSeconds / 60);
    const seconds = this.gameTimeSeconds % 60;
    
    if (minutes === 0) {
      return `${seconds}s`;
    } else if (seconds === 0) {
      return `${minutes}min`;
    } else {
      return `${minutes}min ${seconds}s`;
    }
  }

  onSliderChange() {
    // Update manual input when slider changes
    const minutes = Math.floor(this.gameTimeSeconds / 60);
    const seconds = this.gameTimeSeconds % 60;
    
    if (seconds === 0) {
      this.manualTimeInput = minutes.toString();
    } else {
      this.manualTimeInput = `${minutes}.${Math.floor(seconds / 6)}`; // Convert seconds to decimal (e.g., 30s = 0.5)
    }
  }

  onManualInputChange() {
    // Update slider when manual input changes
    const input = this.manualTimeInput.trim();
    if (input === '') {
      this.gameTimeSeconds = 0;
      return;
    }

    const value = parseFloat(input);
    if (!isNaN(value) && value >= 0) {
      if (input.includes('.')) {
        // Decimal input (e.g., 2.5 = 2min 30s)
        const minutes = Math.floor(value);
        const seconds = Math.round((value - minutes) * 60);
        this.gameTimeSeconds = minutes * 60 + seconds;
      } else {
        // Integer input (e.g., 5 = 5min)
        this.gameTimeSeconds = value * 60;
      }
      
      // Ensure within bounds
      if (this.gameTimeSeconds < 30) this.gameTimeSeconds = 30;
      if (this.gameTimeSeconds > 3600) this.gameTimeSeconds = 3600;
    }
  }

  onCreate() {
    this.roomService.createRoom({    
      gameType: this.selectedGameType,
      gameTimeSeconds: this.gameTimeSeconds
    }).subscribe((json:Game) => {
      this.router.navigate(['/game/' + json.gameId]);
    });
  }

  onClose() {
    this.isOpen=!this.isOpen;
  }
}
