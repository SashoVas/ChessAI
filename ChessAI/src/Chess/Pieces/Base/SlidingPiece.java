package Chess.Pieces.Base;

import Chess.Board;
import Chess.Move;

import java.util.ArrayList;
import java.util.List;

public abstract class SlidingPiece extends Piece{

    public SlidingPiece(int color, int row, int col) {
        super(color, row, col);
    }
    @Override
    protected List<Move> generateMoves(int[][]directions,Board board){
        List<Move>moves=new ArrayList<>() ;
        int initialRow=row;
        int initialCol=col;
        for (int[] direction : directions){
            while(isValid()){
                row+=direction[0];
                col+=direction[1];
                if(isValid()){
                    Piece pieceAtThisPosition=board.getAt(row,col);

                    if(pieceAtThisPosition==null){
                        moves.add(new Move(initialRow,initialCol,row,col));
                        continue;
                    }
                    if(pieceAtThisPosition.color==color){
                        break;
                    }
                    moves.add(new Move(initialRow,initialCol,row,col));
                    break;
                }
            }
            row=initialRow;
            col=initialCol;
        }

        return moves;
    }

    @Override
    protected List<Move> generatePseudoMoves(int[][]directions,Board board){
        List<Move>moves=new ArrayList<>() ;
        int initialRow=row;
        int initialCol=col;
        for (int[] direction : directions){
            while(isValid()){
                row+=direction[0];
                col+=direction[1];
                if(isValid()){
                    Piece pieceAtThisPosition=board.getAt(row,col);

                    if(pieceAtThisPosition==null){
                        moves.add(new Move(initialRow,initialCol,row,col));
                        continue;
                    }
                    moves.add(new Move(initialRow,initialCol,row,col));
                    break;
                }
            }
            row=initialRow;
            col=initialCol;
        }

        return moves;
    }
}
