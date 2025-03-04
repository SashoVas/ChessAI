package Chess.Pieces;

import Chess.Board;
import Chess.Move;
import Chess.Pieces.Base.NonSlidingPiece;
import Chess.Pieces.Base.Piece;

import java.util.List;

public class Knight extends NonSlidingPiece {
    public Knight(int color, int row, int col) {
        super(color, row, col);
    }

    @Override
    public List<Move> getMoves(Board board) {
        int[][] directions={{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2}};
        List<Move>moves=generateMoves(directions,board);
        return moves;
    }


    @Override
    public String getInitial() {
        return color==0?"n":"N";
    }
}
