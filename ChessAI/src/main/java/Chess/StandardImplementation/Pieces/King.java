package Chess.StandardImplementation.Pieces;

import Chess.StandardImplementation.Pieces.Base.NonSlidingPiece;

import java.util.List;

public class King extends NonSlidingPiece {
    public King(int color, int row, int col) {
        super(color, row, col);
    }

    @Override
    public List<Move> getMoves(Board board) {
        //TODO: Implement Castle
        int [][] directions={{1,0},{-1,0},{0,1},{0,-1},{1,1},{-1,1},{1,-1},{-1,-1}};
        List<Move>moves=generateMoves(directions,board);
        return moves;
    }


    @Override
    public String getInitial() {
        return color==0?"k":"K";
    }
}
