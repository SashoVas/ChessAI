export interface Room {
  gameId: number;
  user1Username: string;
  user2Username: string;
  currentFen: string;
  moves: string[];
  user1Color: string;
  user2Color: string;
  user1TimeLeft: number;
  user2TimeLeft: number;
  gameType: string;
  gameStatus: string;
  currentTurnColor: string;
  gameTimeSeconds: number;
  currentTurn: number;
  user1Rating: number;
  user2Rating: number;
  user1IsEloProvisional?: boolean;
  user2IsEloProvisional?: boolean;
}