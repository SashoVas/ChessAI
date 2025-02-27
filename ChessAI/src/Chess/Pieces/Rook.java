package Chess.Pieces;

import Chess.Board;
import Chess.Move;
import Chess.Pieces.Base.Piece;
import Chess.Pieces.Base.SlidingPiece;

import java.security.InvalidParameterException;
import java.util.List;

public class Rook extends SlidingPiece {
    public Rook(int color, int row, int col) {
        super(color, row, col);
    }

    @Override
    public List<Move> getMoves(Board board) {
        //TODO: Implement Castle
        int [][] directions={{1,0},{-1,0},{0,1},{0,-1}};
        List<Move>moves=generateMoves(directions,board);
        if (canCastle(board)){
            moves.add(getCastleMove(board));
        }
        return moves;
    }
    @Override
    public List<Move> getPseudoMoves(Board board) {
        //TODO: Implement Castle
        int [][] directions={{1,0},{-1,0},{0,1},{0,-1}};
        List<Move>moves=generatePseudoMoves(directions,board);
        if (canCastle(board)){
            moves.add(getCastleMove(board));
        }
        return moves;
    }

    @Override
    public String getInitial() {
        return color==0?"r":"R";
    }

    public Move getCastleMove(Board board){
        if(!canCastle(board)) {
            throw new InvalidParameterException("Can not castle");
        }
        Move move=new Move(row,col,row,col==0?3:5);
        move.isCastle=true;
        return move;
    }
    public boolean canCastle(Board board){
        if(isMoved){
            return false;
        }
        Piece king=board.getPiece(color==1?"K":"k").get(0);
        if (king.isMoved || king.row !=row || king.row!=(color==1?7:0) || king.col!=4){
            return false;
        }
        if(king.col>this.col){
            return board.getAt(king.row,1) == null
                    && board.getAt(king.row,2) == null
                    && board.getAt(king.row,3) == null ;
        }

        return board.getAt(king.row,5) == null
                && board.getAt(king.row,6) == null;

    }
}
