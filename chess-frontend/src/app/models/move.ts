
export interface Move {
    fen: string;
    move: string;
    nextMoves: string[];
    gameState:string;
    currentColor:string;
    colorOfRequestUser:string;
    gameType:string;
}