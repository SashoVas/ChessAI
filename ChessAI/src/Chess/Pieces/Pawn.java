package Chess.Pieces;


import Chess.Board;
import Chess.Move;
import Chess.Pieces.Base.NonSlidingPiece;
import Chess.Pieces.Base.Piece;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends NonSlidingPiece {
    public Pawn(int color, int row, int col) {
        super(color, row, col);
    }
    private List<Move>getPromotions(Move move){
        String[] pieceInitials={color==0?"n":"N",color==0?"r":"R",color==0?"b":"B",color==0?"q":"Q"};
        List<Move>moves=new ArrayList<>();
        for(String initial:pieceInitials){
            Move newMove=new Move(move.initialRow,move.initialCol,move.targetRow,move.targetCol);
            newMove.isPromotion=true;
            newMove.promotionPiece=initial;
            moves.add(newMove);
        }
        return moves;
    }

    private List<Move> generateEnPassantMoves(Board board){
        List<Move>moves=new ArrayList<>();
        if (color==0 && row==4){
            Piece left=col==0?null:board.getAt(4, col - 1);
            Piece right=col==7?null:board.getAt(4, col + 1);
            if(left!=null && left.color!=color && col>0 && left instanceof Pawn){
                Move lastMove=board.getLastMove();
                Piece lastMoved=board.getAt(lastMove.targetRow,lastMove.targetCol);
                if(lastMove.initialRow==row+2 && lastMove.initialCol==col-1 && board.getAt(row+1,col-1)==null && lastMoved instanceof Pawn){

                    Move move=new Move(row,col,row+1,col-1);
                    move.isEnPassant=true;
                    moves.add(move);
                }
            }
            if(right!=null && right.color!=color&& col<7 && right instanceof Pawn){
                Move lastMove=board.getLastMove();
                Piece lastMoved=board.getAt(lastMove.targetRow,lastMove.targetCol);

                if(lastMove.initialRow==row+2 && lastMove.initialCol==col+1 && board.getAt(row+1,col+1)==null && lastMoved instanceof Pawn ){
                    Move move=new Move(row,col,row+1,col+1);
                    move.isEnPassant=true;
                    moves.add(move);
                }
            }
        }
        else if(color==1&& row==3){
            Piece left=col==0?null:board.getAt(3, col - 1);
            Piece right=col==7?null:board.getAt(3, col + 1);
            if(left!=null && left.color!=color && col>0 && left instanceof Pawn){
                Move lastMove=board.getLastMove();
                Piece lastMoved=board.getAt(lastMove.targetRow,lastMove.targetCol);

                if(lastMove.initialRow==row-2 && lastMove.initialCol==col-1 && board.getAt(row-1,col-1)==null && lastMoved instanceof Pawn){
                    Move move=new Move(row,col,row-1,col-1);
                    move.isEnPassant=true;
                    moves.add(move);
                }
            }
            if(right!=null && right.color!=color&& col<7 && right instanceof Pawn){
                Move lastMove=board.getLastMove();
                Piece lastMoved=board.getAt(lastMove.targetRow,lastMove.targetCol);

                if(lastMove.initialRow==row-2 && lastMove.initialCol==col+1 && board.getAt(row-1,col+1)==null && lastMoved instanceof Pawn){
                    Move move=new Move(row,col,row-1,col+1);
                    move.isEnPassant=true;
                    moves.add(move);
                }
            }
        }
        return moves;
    }
    private List<Move> generateAttackingMoves(Board board){
        List<Move>moves=new ArrayList<>();
        int moveDirection=color==1?-1:1;
        int[][] attackDirections={{moveDirection,1},{moveDirection,-1}};
        int initialRow=row;
        int initialCol=col;
        for(int[] direction: attackDirections){
            row+=direction[0];
            col+=direction[1];
            if(isValid()){
                Piece pieceAtPosition=board.getAt(row,col);
                if(pieceAtPosition!=null && pieceAtPosition.color!=color){
                    Move move=new Move(initialRow,initialCol,row,col);
                    if(row==0 || row==7) {
                        moves.addAll(getPromotions(move));
                    }
                    else{
                        moves.add(move);
                    }
                }
            }
            row=initialRow;
            col=initialCol;
        }
        return moves;
    }
    private List<Move> generateNonAttackingMoves(Board board){
        List<Move>moves=new ArrayList<>();
        int moveDirection=color==1?-1:1;
        //Generate standard one-step move
        if(board.getAt(row+ moveDirection,col)==null){
            Move move=new Move(row,col,row+moveDirection,col);
            if(row+moveDirection==0 || row+moveDirection==7){
                moves.addAll(getPromotions(move));
            }
            else{
                moves.add(move);
            }
        }

        //Generating two move up if at second row
        if(color==1 && row==6 && board.getAt(5,col)==null && board.getAt(4,col)==null){
            int[][] doubleMove={{-2,0}};
            moves.addAll(generateMoves(doubleMove,board));
        }
        else if(color==0 && row==1&& board.getAt(2,col)==null&& board.getAt(3,col)==null){
            int[][] doubleMove={{2,0}};
            moves.addAll(generateMoves(doubleMove,board));
        }
        return moves;
    }
    @Override
    public List<Move> getMoves(Board board) {
        //TODO: Implement en passant

        List<Move>moves=new ArrayList<>();
        moves.addAll(generateNonAttackingMoves(board));
        moves.addAll(generateAttackingMoves(board));
        moves.addAll(generateEnPassantMoves(board));

        return moves;
    }

    private List<Move> generatePseudoAttackingMoves(Board board){
        List<Move>moves=new ArrayList<>();
        int moveDirection=color==1?-1:1;
        int[][] attackDirections={{moveDirection,1},{moveDirection,-1}};
        int initialRow=row;
        int initialCol=col;
        for(int[] direction: attackDirections){
            row+=direction[0];
            col+=direction[1];
            if(isValid()){
                Piece pieceAtPosition=board.getAt(row,col);

                Move move=new Move(initialRow,initialCol,row,col);
                if(row==0 || row==7) {
                    moves.addAll(getPromotions(move));
                }
                else{
                    moves.add(move);
                }

            }
            row=initialRow;
            col=initialCol;
        }
        return moves;
    }
    @Override
    public List<Move> getPseudoMoves(Board board) {
        //TODO: Implement en passant

        List<Move>moves=generatePseudoAttackingMoves(board);

        return moves;
    }

    @Override
    public String getInitial() {
        return color==0?"p":"P";
    }
}
