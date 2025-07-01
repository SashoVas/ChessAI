import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { RoomServiceService } from '../../services/room-service.service';
import { UserStatisticsService, UserStatistics, LeaderboardUser, EloConfig } from '../../services/user-statistics.service';
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
  eloConfig: EloConfig | null = null;
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
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      if (event.url === '/') {
        this.loadLeaderboard();
        if (this.authService.isLoggedIn()) {
          this.loadUserStatistics();
        }
      }
    });
  }

  ngOnInit() {
    this.loadEloConfig();
    this.loadLeaderboard();
    if (this.authService.isLoggedIn()) {
      this.username = this.authService.getUsername();
      this.loadUserStatistics();
    }
  }

  loadEloConfig() {
    this.userStatisticsService.getEloConfig().subscribe({
      next: (config) => {
        this.eloConfig = config;
      },
      error: (err) => {
        console.error('Error loading ELO config:', err);
      }
    });
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

  get remainingGamesForRealElo(): number {
    if (!this.userStats?.isEloProvisional) {
      return 0; // Already has real ELO
    }
    
    const totalGames = this.userStats?.totalGames || 0;
    const minimumGamesForElo = this.eloConfig?.minimumGamesForElo || 5; // Use config value or fallback
    const remaining = minimumGamesForElo - totalGames;
    
    return Math.max(0, remaining);
  }

  getEloProgressPercentage(): number {
    if (!this.userStats?.isEloProvisional) {
      return 100; // Already has real ELO
    }
    
    const totalGames = this.userStats?.totalGames || 0;
    const minimumGamesForElo = this.eloConfig?.minimumGamesForElo || 5;
    const percentage = (totalGames / minimumGamesForElo) * 100;
    
    return Math.min(100, Math.max(0, percentage));
  }
}
