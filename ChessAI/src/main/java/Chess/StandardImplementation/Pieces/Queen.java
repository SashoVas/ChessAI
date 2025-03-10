package Chess.StandardImplementation.Pieces;

import Chess.StandardImplementation.Pieces.Base.SlidingPiece;

import java.util.List;

public class Queen extends SlidingPiece {
    public Queen(int color, int row, int col) {
        super(color, row, col);
    }

    @Override
    public List<Move> getMoves(Board board) {
        int [][] directions={{1,0},{-1,0},{0,1},{0,-1},{1,1},{-1,1},{1,-1},{-1,-1}};
        List<Move>moves=generateMoves(directions,board);
        return moves;
    }

    @Override
    public String getInitial() {
        return color==0?"q":"Q";
    }
}
