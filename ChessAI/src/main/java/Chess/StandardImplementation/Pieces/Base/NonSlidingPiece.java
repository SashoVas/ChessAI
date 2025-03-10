package Chess.StandardImplementation.Pieces.Base;

import Chess.StandardImplementation.Pieces.Board;
import Chess.StandardImplementation.Pieces.Move;

import java.util.ArrayList;
import java.util.List;

public abstract class NonSlidingPiece extends Piece{
    public NonSlidingPiece(int color, int row, int col) {
        super(color, row, col);
    }
    @Override
    protected List<Move> generateMoves(int[][]directions,Board board){
        List<Move>moves=new ArrayList<>() ;
        int initialRow=row;
        int initialCol=col;
        for(int[] direction:directions){
            row+=direction[0];
            col+=direction[1];
            if(isValid()){
                Piece pieceAtPosition=board.getAt(row,col);
                if(pieceAtPosition==null || pieceAtPosition.color!=color){
                    moves.add(new Move(initialRow,initialCol,row,col));
                }
            }

            row=initialRow;
            col=initialCol;
        }

        return moves;
    }
}
