import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Room } from '../../models/room';
import { RoomServiceService } from '../../services/room-service.service';
import { Router } from '@angular/router';
import { Game } from '../../models/game';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-available-rooms',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './available-rooms.component.html',
  styleUrl: './available-rooms.component.css'
})
export class AvailableRoomsComponent {
    games: Room[] = [];

  constructor(
    private roomService: RoomServiceService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadAvailableGames();
  }

  loadAvailableGames(): void {
    this.roomService.getFreeRooms().subscribe(rooms=>{
      this.games=rooms;
    })
  }

  joinGame(gameId: number): void {
      this.roomService.joinRoom(gameId.toString()).subscribe({
        next: (msg: Game) => {
           this.router.navigate(['/game/' + gameId]);
        },
        error: (error) => {
          console.error('Failed to join game:', error);
          if (error.status === 401) {
            alert('You cannot join this game. You may be trying to join your own game or the game is no longer available.');
          } else {
            alert('Failed to join game. Please try again.');
          }
        }
      })
  }

  canJoin(game: Room): boolean {
    const currentUsername = this.authService.getUsername();
    return !game.user2Username && game.user1Username !== currentUsername;
  }

  isOwnGame(game: Room): boolean {
    const currentUsername = this.authService.getUsername();
    return game.user1Username === currentUsername;
  }

  goToCreateGame(): void {
    this.router.navigate(['/create-game'], { queryParams: { showForm: 'true' } });
  }
}
