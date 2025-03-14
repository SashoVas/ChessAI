package Chess.StandardImplementation.Pieces;

import Chess.StandardImplementation.Pieces.Base.SlidingPiece;

import java.util.List;

public class Bishop extends SlidingPiece {
    public Bishop(int color, int row, int col) {
        super(color, row, col);
    }

    @Override
    public List<Move> getMoves(Board board) {
        int [][] directions={{1,1},{-1,1},{1,-1},{-1,-1}};
        List<Move>moves=generateMoves(directions,board);
        return moves;
    }

    @Override
    public String getInitial() {
        return this.color==0?"b":"B";
    }
}
