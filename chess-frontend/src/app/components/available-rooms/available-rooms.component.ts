import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Room } from '../../models/room';
import { RoomServiceService } from '../../services/room-service.service';

@Component({
  selector: 'app-available-rooms',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './available-rooms.component.html',
  styleUrl: './available-rooms.component.css'
})
export class AvailableRoomsComponent {
    games: Room[] = [];

  constructor(private roomService: RoomServiceService) {}

  ngOnInit(): void {
    this.loadAvailableGames();
  }

  loadAvailableGames(): void {
    this.roomService.getFreeRooms().subscribe(rooms=>{
      this.games=rooms;
    })
  }

  joinGame(gameId: number): void {

  }

  spectateGame(gameId: number): void {

  }

  canJoin(game: Room): boolean {
    return  !game.user2Username;
  }
}
