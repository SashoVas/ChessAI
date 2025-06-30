import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { RoomServiceService } from '../../services/room-service.service';
import { AuthService } from '../../services/auth.service';
import { Game } from '../../models/game';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-game-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './game-history.component.html',
  styleUrl: './game-history.component.css'
})
export class GameHistoryComponent implements OnInit, OnDestroy {
  games: Game[] = [];
  isLoading = false;
  errorMessage = '';
  username = '';
  filteredGames: Game[] = [];
  private destroy$ = new Subject<void>();

  constructor(
    private roomService: RoomServiceService, 
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.username = this.authService.getUsername();
    this.loadGames();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadGames(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.roomService.getUserGames(this.username).pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: (games) => {
        console.log('Received games:', games); // Debug log
        // Filter out NOT_STARTED games and sort by createdAt in descending order (newest first)
        this.games = games
          .filter(game => game.gameStatus !== 'NOT_STARTED')
          .sort((a, b) => {
            if (!a.createdAt && !b.createdAt) return 0;
            if (!a.createdAt) return 1;
            if (!b.createdAt) return -1;
            return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
          });
        this.filteredGames = this.games;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error fetching games:', error);
        this.errorMessage = 'Failed to load game history.';
        this.games = [];
        this.filteredGames = [];
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
