package Chess;

import Chess.Pieces.*;
import Chess.Pieces.Base.Piece;

import java.security.InvalidParameterException;
import java.util.*;

public class Board {
    public static final String defaultFen="rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
    private Piece[][]board;
    //TODO: Use a hash table
    private List<Piece> pieces;
    private Stack<ReverseMove>reverseMoves;

    protected Board(){
        board=new Piece[8][8];
        pieces=new ArrayList<>();
        reverseMoves=new Stack<>();
    }

    public int search(int color,int depth){
        if (depth==0){
            return 1;
        }

        List<Move>moves=getMoves(color);
        int movesCount=0;
        for (Move move: moves){
            //for (int i = 0; i < 1 - depth; i++) {
            //    System.out.print("  ");
            //}
            //String moveString1=move.toString();
            //System.out.print(moveString1);
            //System.out.println(",");
            if(makeAMoveSearch(move))
            {

                int currentMoves = search(1 - color, depth - 1);
                movesCount += currentMoves;
                undoMove();
                //if (depth==4){
                //    String moveString=move.toString();
                //    System.out.print(moveString);
                //    System.out.print(": ");
                //    System.out.println(currentMoves);
                //}


            }

        }

        return movesCount;
    }
    public void makeAMove(Move move){
        //TODO: Validate castle, Checks and piece capture and every other validation
        if (board[move.initialRow][move.initialCol]==null){
            throw new InvalidParameterException("Invalid move");
        }
        int color=getAt(move.initialRow,move.initialCol).color;
        List<Move>moves=getMoves(color);
        if (!moves.contains(move)){
            throw new InvalidParameterException("Invalid move");
        }
    }
    public boolean makeAMoveSearch(Move move){
        int color=getAt(move.initialRow,move.initialCol).color;
        //TODO:Refactor
        if(move.isCastle) {
            if (isInCheck(color)){
                return false;

            }
            if (move.initialCol<4
                    && ( isAttacked(move.initialRow,2,color)
                    || isAttacked(move.initialRow,3,color))){
                return false;

            }
            else if(move.initialCol>4
                    && (isAttacked(move.initialRow,5,color)
                    || isAttacked(move.initialRow,6,color))){
                return false;

            }
        }
        moveAPiece(move);
        if(isInCheck(color)){
            undoMove();
            return false;
        }
        return true;
    }
    public Move getLastMove(){
        if (reverseMoves.empty()){
            return null;
        }
        ReverseMove lastReversed=reverseMoves.peek();
        Move lastMove=new Move(lastReversed.targetRow,lastReversed.targetCol,lastReversed.initialRow,lastReversed.initialCol);
        return lastMove;
    }
    public boolean isInCheck(int color){
        String kingInitial=color==0?"k":"K";
        Piece king=getPiece(kingInitial).get(0);
        return isAttacked(king.row,king.col,color);
    }
    public boolean isAttacked(int row,int col,int color){
        List<Move>moves=getMoves(1-color);
        for(Move move:moves){
            if (move.targetRow==row && move.targetCol==col){
                return true;
            }
        }
        //Checks if is attacked by pawn
        if (color==0 && row<7){
            if((col>0 && board[row+1][col-1] instanceof Pawn && board[row+1][col-1].color!=color) ||
                (col<7 && board[row+1][col+1] instanceof Pawn && board[row+1][col+1].color!=color)){
                return true;
            }
        }
        else if(color==1 && row>0){
            if((col>0 && board[row-1][col-1] instanceof Pawn && board[row-1][col-1].color!=color) ||
                    (col<7 && board[row-1][col+1] instanceof Pawn && board[row-1][col+1].color!=color)){
                return true;
            }
        }
        return false;
    }
    private void moveAPiece(Move move){
        //TODO: Implement promotions
        //This method does not validate the move!
        if (move.isCastle){
            int kingColumn=4;
            Piece rook=getAt(move.initialRow,move.initialCol);
            Piece king=getAt(move.initialRow,kingColumn);
            rook.move(move.targetRow,move.targetCol);
            board[move.initialRow][move.initialCol]=null;
            board[move.targetRow][move.targetCol]=rook;
            if (move.initialCol<king.col){
                king.move(king.row,king.col-2);
            }
            else{
                king.move(king.row,king.col+2);
            }
            board[king.row][king.col]=king;
            board[king.row][kingColumn]=null;
            ReverseMove reverseMove=new ReverseMove(move.targetRow,move.targetCol,move.initialRow,move.initialCol,null,true,false,true,false);
            reverseMoves.push(reverseMove);
            return;
        }
        Piece piece=getAt(move.initialRow,move.initialCol);
        board[move.initialRow][move.initialCol]=null;
        Piece oldPiece=board[move.targetRow][move.targetCol];
        if (oldPiece!=null){
            pieces.remove(oldPiece);
        }
        board[move.targetRow][move.targetCol]=piece;
        boolean isFirstMove=!piece.isMoved;
        piece.move(move.targetRow,move.targetCol);

        if(move.isEnPassant){
            Piece takenPawn=getAt(move.initialRow,move.targetCol);
            pieces.remove(takenPawn);

            board[move.initialRow][move.targetCol]=null;
            ReverseMove reverseMove=new ReverseMove(move.targetRow,move.targetCol,move.initialRow,move.initialCol,takenPawn,false,true,true,false);
            reverseMoves.push(reverseMove);
            return;
        }
        if(move.isPromotion){
            Piece promotionPiece=pieceFactory(move.promotionPiece,move.targetRow,move.targetCol);
            promotionPiece.isMoved=true;
            pieces.remove(piece);
            pieces.add(promotionPiece);
            board[move.targetRow][move.targetCol]=promotionPiece;
            ReverseMove reverseMove=new ReverseMove(move.targetRow,move.targetCol,move.initialRow,move.initialCol,oldPiece,false,false,false,true);
            reverseMoves.push(reverseMove);
            return;
        }
        ReverseMove reverseMove=new ReverseMove(move.targetRow,move.targetCol,move.initialRow,move.initialCol,oldPiece,false,false,isFirstMove,false);
        reverseMoves.push(reverseMove);
    }
    private void undoMove(){
        ReverseMove revMove=reverseMoves.pop();
        //moveAPiece(revMove);
        moveAPiece(new Move(revMove.initialRow,revMove.initialCol,revMove.targetRow,revMove.targetCol));
        reverseMoves.pop();
        Piece movedPiece=getAt(revMove.targetRow,revMove.targetCol);
        movedPiece.isMoved=!revMove.firstMove;
        if(!revMove.isEnPassant){
            board[revMove.initialRow][revMove.initialCol]=revMove.takenPiece;
        }
        if (revMove.takenPiece!=null){
            pieces.add(revMove.takenPiece);
        }
        if (revMove.isCastle){
            if(revMove.targetCol==0){
                Piece king=board[revMove.initialRow][2];
                board[revMove.initialRow][4]=king;
                board[revMove.initialRow][2]=null;
                king.move(revMove.initialRow,4);
                king.isMoved=false;
            }
            else{
                Piece king=board[revMove.initialRow][6];
                board[revMove.initialRow][4]=king;
                board[revMove.initialRow][6]=null;
                king.move(revMove.initialRow,4);
                king.isMoved=false;
            }
        }
        if(revMove.isEnPassant){
            board[revMove.targetRow][revMove.initialCol]=revMove.takenPiece;
        }
        if(revMove.isPromotion){
            Piece oldPawn=pieceFactory(movedPiece.color==0?"p":"P",revMove.targetRow,revMove.targetCol);
            oldPawn.isMoved=true;
            pieces.remove(movedPiece);
            pieces.add(oldPawn);
            board[revMove.targetRow][revMove.targetCol]=oldPawn;

        }
    }
    public List<Piece> getPiece(String initial){
        List<Piece>res=new ArrayList<>();
        for(Piece piece : pieces){
            if (Objects.equals(piece.getInitial(), initial)) {
                res.add(piece);
            }
        }
        return res;
    }
    public void printBoard(){
        for (int i=0;i<8;i++){
            System.out.printf("|");
            for (int j=0;j<8;j++){
                if(board[i][j]==null){
                    System.out.printf(" |");
                    continue;
                }
                System.out.printf(board[i][j].getInitial());
                System.out.printf("|");
            }
            System.out.println();
        }
    }
    public List<Move> getMoves(int color){
        List<Move>moves=new ArrayList<>();
        for(Piece piece: pieces){
            if(piece.color==color){
                moves.addAll(piece.getMoves(this));
            }
        }
        return moves;
    }
    public Piece getAt(int row,int col){
        if(row<0 || row>7 ||col<0 ||col>7){
            throw new InvalidParameterException("Invalid position");
        }
        return board[row][col];
    }

    public static Board fenToBoard(String fen){

        String fenToIterate=fen.toLowerCase();

        Board board=new Board();
        int row=0;
        int col=0;
        for (int i=0;i<fen.length();i++){
            if(fenToIterate.charAt(i)=='/'){
                row++;
                col=0;
            }
            else if(fenToIterate.charAt(i)<='9'){
                col+=fenToIterate.charAt(i)-'0';
            }
            else{
                Piece piece;
                switch (fenToIterate.charAt(i)) {
                    case 'k':
                        piece = new King(fen.charAt(i) < 'Z' ? 1 : 0, row, col);
                        break;
                    case 'n':
                        piece = new Knight(fen.charAt(i) < 'Z' ? 1 : 0, row, col);
                        break;
                    case 'r':
                        piece = new Rook(fen.charAt(i) < 'Z' ? 1 : 0, row, col);
                        break;
                    case 'b':
                        piece = new Bishop(fen.charAt(i) < 'Z' ? 1 : 0, row, col);
                        break;
                    case 'q':
                        piece = new Queen(fen.charAt(i) < 'Z' ? 1 : 0, row, col);
                        break;
                    case 'p':
                        piece = new Pawn(fen.charAt(i) < 'Z' ? 1 : 0, row, col);
                        break;
                    default:
                        piece = new King(-1, -1, -1);
                }
                board.board[row][col]=piece;
                board.pieces.add(piece);
                col++;
            }
        }
        return board;
    }
    public static Piece pieceFactory(String initial,int row,int col){
        Piece piece;
        switch (initial.toLowerCase().charAt(0)) {
            case 'k':
                piece = new King(initial.charAt(0) < 'Z' ? 1 : 0, row, col);
                break;
            case 'n':
                piece = new Knight(initial.charAt(0) < 'Z' ? 1 : 0, row, col);
                break;
            case 'r':
                piece = new Rook(initial.charAt(0) < 'Z' ? 1 : 0, row, col);
                break;
            case 'b':
                piece = new Bishop(initial.charAt(0) < 'Z' ? 1 : 0, row, col);
                break;
            case 'q':
                piece = new Queen(initial.charAt(0) < 'Z' ? 1 : 0, row, col);
                break;
            case 'p':
                piece = new Pawn(initial.charAt(0) < 'Z' ? 1 : 0, row, col);
                break;
            default:
                piece = new King(-1, -1, -1);
        }
        return piece;
    }
}
