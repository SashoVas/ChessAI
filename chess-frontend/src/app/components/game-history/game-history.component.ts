import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RoomServiceService } from '../../services/room-service.service';
import { AuthService } from '../../services/auth.service';
import { Game } from '../../models/game';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-game-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './game-history.component.html',
  styleUrl: './game-history.component.css'
})
export class GameHistoryComponent implements OnInit {
  games: Game[] = [];
  isLoading = false;
  errorMessage = '';
  username = '';
  filteredGames: Game[] = [];

  constructor(
    private roomService: RoomServiceService, 
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.username = this.authService.getUsername();
    this.isLoading = true;
    this.roomService.getUserGames(this.username).subscribe({
      next: (games) => {
        this.games = games;
        this.filteredGames = games;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error fetching games:', error);
        this.errorMessage = 'Failed to load game history.';
        this.games = [];
        this.isLoading = false;
      }
    });
  }

  getGameStatusDisplay(status: string): string {
    switch (status) {
      case 'WINNER_WHITE':
        return 'White Wins';
      case 'WINNER_BLACK':
        return 'Black Wins';
      case 'IN_PROGRESS':
        return 'In Progress';
      case 'NOT_STARTED':
        return 'Not Started';
      case 'DRAW':
        return 'Draw';
      case 'UNKNOWN':
        return 'Not Started';
      default:
        return status;
    }
  }

  isWinner(game: Game): boolean {
    const isUser1 = game.user1Username === this.username;
    const isUser2 = game.user2Username === this.username;
    
    if (game.gameStatus === 'WINNER_WHITE') {
      return (isUser1 && game.user1Color === 'WHITE') || (isUser2 && game.user2Color === 'WHITE');
    } else if (game.gameStatus === 'WINNER_BLACK') {
      return (isUser1 && game.user1Color === 'BLACK') || (isUser2 && game.user2Color === 'BLACK');
    }
    
    return false;
  }
}
