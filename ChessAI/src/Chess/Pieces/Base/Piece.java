package Chess.Pieces.Base;

import Chess.Board;
import Chess.Move;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Piece {

    public int id;
    public int color;
    public int row;
    public int col;
    public boolean isMoved=false;
    public Piece(int color,int row,int col){
        this.color=color;
        this.row=row;
        this.col=col;
    }

    public abstract List<Move> getMoves(Board board);
    public abstract List<Move> getPseudoMoves(Board board);

    //public abstract boolean move(int targetRow,int targetCol);
    public boolean move(int targetRow,int targetCol){
        row=targetRow;
        col=targetCol;
        isMoved=true;
        if(!isValid()){
            throw new InvalidParameterException("Invalid move in piece");
        }
        return true;
    }
    public abstract String getInitial();
    protected abstract List<Move> generateMoves(int[][]directions,Board board);
    protected abstract List<Move> generatePseudoMoves(int[][]directions,Board board);
    public boolean isValid(){
        return color>=0 && color<=1 && row<=7 && row>=0 && col<=7 && col>=0;
    }

    @Override
    public int hashCode() {
        //return Objects.hash(id);
        return id;
    }
}
