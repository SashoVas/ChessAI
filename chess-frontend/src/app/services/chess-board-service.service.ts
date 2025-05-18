import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ChessBoardServiceService {

  constructor() { }

  blackPieces: Record<string,string> = {
    k: '♚',
    q: '♛',
    n: '♞',
    r: '♜',
    b: '♝',
    p: '♟'
  };
  whitePieces: Record<string,string> = {
    K: '♔',
    Q: '♕',
    N: '♘',
    R: '♖',
    B: '♗',
    P: '♙'
  };
  readonly firstBoardColor = '#f0d9b5';
  readonly secondBoardColor = '#b58863';
  readonly highlightColor = '#a9a9a9';

  readonly boardSize = 8;

  public fenToBoard(fen: string): string[][] {
    let initialPosition = Array(8).fill(null).map(() => Array(8).fill(''));
    let idx = 0;
    const boardFen = fen.split(' ')[0];
    for (const ch of boardFen) {
      if (ch === '/') continue;
      if (!isNaN(+ch)) {
        idx += +ch;
      } else {
        const row = Math.floor(idx / 8);
        const col = idx % 8;
        const dict = ch === ch.toUpperCase() ? this.whitePieces : this.blackPieces;
        const key = ch === ch.toUpperCase() ? ch : ch.toLowerCase();
        initialPosition[row][col] = dict[key];
        idx++;
      }
    }
    return initialPosition;
  }

  public toChessAlgebra(pos: number, black: boolean): string {
    const row = black ? Math.floor(pos / 8) + 1 : 8 - Math.floor(pos / 8);
    const col = pos % 8;
    return String.fromCharCode('a'.charCodeAt(0) + col) + row;
  }

  public fromAlgebra(move: string, black: boolean): number {
    if (black) {
      return 8 * (+move[1] - 1) + (move.charCodeAt(0) - 'a'.charCodeAt(0));
    } else {
      return 8 * (8 - +move[1]) + (move.charCodeAt(0) - 'a'.charCodeAt(0));
    }
  }

  public getAttackedPositions(from:number,currentColor:string,possibleMoves:string[]): number[] {
    const prefix = this.toChessAlgebra(from, currentColor === 'BLACK');
    return possibleMoves
      .filter(m => m.startsWith(prefix))
      .map(m => this.fromAlgebra(m.substring(2, 4), currentColor === 'BLACK'));
  }

  public createChessboard(board:HTMLDivElement,initialPosition:string[][],currentColor:string ): void {

    board.innerHTML = '';

    for (let row = 0; row < this.boardSize; row++) {
      for (let col = 0; col < this.boardSize; col++) {
        const square = document.createElement('div');
        square.className = 'square';
        square.style.backgroundColor =
          (row + col) % 2 === 0 ? this.firstBoardColor : this.secondBoardColor;

        const piece = currentColor === 'BLACK'
          ? initialPosition[7 - row][col]
          : initialPosition[row][col];

        if (piece) {
          const pieceEl = document.createElement('div');
          pieceEl.className = 'piece';
          pieceEl.textContent = piece;
          square.appendChild(pieceEl);
        }

        board?.appendChild(square);
      }
    }
  }

  public highlightSquare(board:HTMLDivElement,pos: number): void {
    const sq = board.children[pos] as HTMLElement;
    Object.assign(sq.style, {
      backgroundColor: this.highlightColor,
      borderRadius: '10px'
    });
  }

  public unhighlightSquare(board:HTMLDivElement,pos: number): void {
    const sq = board.children[pos] as HTMLElement;
    const row = Math.floor(pos / 8), col = pos % 8;
    sq.style.backgroundColor =
      (row + col) % 2 === 0 ? this.firstBoardColor : this.secondBoardColor;
    sq.style.borderRadius = '';
  }

}
