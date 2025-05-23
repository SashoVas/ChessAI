export interface Game {
  gameId: string;
  user1Color: string;
  user2Color: string;
  user1Username: string ;
  user2Username: string;
  currentFen: string;
  moves:string[];
  user1TimeLeft: number;
  user2TimeLeft: number;
  gameType: string ;
  gameStatus: string;
  currentTurnColor: string;
  gameTimeSeconds: number;
  currentTurn:number ;

}