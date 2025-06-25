import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { RoomServiceService } from '../../services/room-service.service';
import { UserStatisticsService, UserStatistics, LeaderboardUser } from '../../services/user-statistics.service';
import { CreateGameComponent } from '../create-game/create-game.component';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [CommonModule, CreateGameComponent],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css'
})
export class HomePageComponent implements OnInit {
  username: string = '';
  userStats: UserStatistics | null = null;
  leaderboard: LeaderboardUser[] = [];
  timeControls = [
    { name: 'Bullet', time: 1 },
    { name: 'Blitz', time: 3 },
    { name: 'Rapid', time: 10 },
    { name: 'Classical', time: 30 }
  ];

  constructor(
    public authService: AuthService,
    private roomService: RoomServiceService,
    private userStatisticsService: UserStatisticsService,
    private router: Router
  ) {
    // Listen for navigation events to refresh data when returning to home page
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      if (event.url === '/' && this.authService.isLoggedIn()) {
        //refresh data
        this.loadUserStatistics();
        this.loadLeaderboard();
      }
    });
  }

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.username = this.authService.getUsername();
      this.loadUserStatistics();
      this.loadLeaderboard();
    }
  }

  loadUserStatistics() {
    this.userStatisticsService.getUserStatistics().subscribe({
      next: (stats) => {
        this.userStats = stats;
      },
      error: (err) => {
        console.error('Error loading user statistics:', err);
      }
    });
  }

  loadLeaderboard() {
    this.userStatisticsService.getLeaderboard(5).subscribe({
      next: (data) => {
        this.leaderboard = data;
      },
      error: (err) => {
        console.error('Error loading leaderboard:', err);
      }
    });
  }

  createGameFromPredefinedTimeControls(timeControl: any) {
    const gameData = {
      gameType: 'MULTIPLAYER',
      gameTimeSeconds: timeControl.time * 60
    };
    
    this.roomService.createRoom(gameData).subscribe({
      next: (response) => {
        this.router.navigate(['/game', response.gameId]);
      },
      error: (err) => {
        console.error('Error creating game:', err);
      }
    });
  }

  // Getters for template
  get wins(): number {
    return this.userStats?.wins || 0;
  }

  get losses(): number {
    return this.userStats?.losses || 0;
  }

  get draws(): number {
    return this.userStats?.draws || 0;
  }

  get lastFinishedGame(): any {
    return this.userStats?.lastGameId;
  }

  get result(): string {
    return this.userStats?.lastGameResult || '';
  }

  get opponentName(): string {
    return this.userStats?.lastGameOpponent || '';
  }
}
