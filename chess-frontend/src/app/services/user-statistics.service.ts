import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface UserStatistics {
  username: string;
  eloRating: number;
  isEloProvisional: boolean;
  totalGames: number;
  wins: number;
  losses: number;
  draws: number;
  winRate: number;
  lastGameId?: number;
  lastGameResult?: string;
  lastGameOpponent?: string;
}

export interface LeaderboardUser {
  username: string;
  eloRating: number;
  isEloProvisional: boolean;
}

export interface EloConfig {
  minimumGamesForElo: number;
}

@Injectable({
  providedIn: 'root'
})
export class UserStatisticsService {
  private apiUrl = 'http://localhost:8080';
  private userStatsSubject = new BehaviorSubject<UserStatistics | null>(null);
  public userStats$ = this.userStatsSubject.asObservable();

  constructor(private http: HttpClient) {}

  getUserStatistics(): Observable<UserStatistics> {
    const headers = this.getAuthHeaders();
    
    return this.http.get<UserStatistics>(`${this.apiUrl}/profile`, { 
      headers: headers 
    }).pipe(
      tap(stats => this.userStatsSubject.next(stats))
    );
  }

  getLeaderboard(limit: number = 5): Observable<LeaderboardUser[]> {
    return this.http.get<LeaderboardUser[]>(`${this.apiUrl}/leaderboard?limit=${limit}`);
  }

  getEloConfig(): Observable<EloConfig> {
    return this.http.get<EloConfig>(`${this.apiUrl}/elo-config`);
  }

  refreshUserStatistics(): void {
    this.getUserStatistics().subscribe();
  }

  getCurrentUserStats(): UserStatistics | null {
    return this.userStatsSubject.value;
  }

  private getAuthHeaders() {
    const token = localStorage.getItem('authToken');
    return {
      'Authorization': `Bearer ${token}`
    };
  }
} 