package Chess;

import Chess.Pieces.*;
import Chess.Pieces.Base.Piece;

import java.security.InvalidParameterException;
import java.util.*;

public class Board {
    private static int boardPieces=0;
    private Piece blackKing;
    private Piece whiteKing;
    public static final String defaultFen="rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
    private Piece[][]board;
    //TODO: Use a hash table
    private Set<Piece> pieces;
    private Stack<ReverseMove>reverseMoves;
    private List<Set<Piece>> attackedSquaresWhite;
    private List<Set<Piece>> attackedSquaresBlack;
    private HashMap<Piece,List<Move>>pieceMovesWhite;
    private HashMap<Piece,List<Move>>pieceMovesBlack;

    protected Board(){
        board=new Piece[8][8];
        pieces=new HashSet<>();
        reverseMoves=new Stack<>();
    }
    private List<Set<Piece>> getAttackedSquares(int color){
        return color==1?attackedSquaresWhite:attackedSquaresBlack;
    }
    private HashMap<Piece,List<Move>> getPiecesMoves(int color){
        return color==1?pieceMovesWhite:pieceMovesBlack;
    }
    public int search2(int color,int depth){
        if (depth==0){
            return 1;
        }
        List<Piece>rightPieces=new ArrayList<>();
        for(Piece piece: pieces) {
            if(piece.color!=color){
                continue;
            }
            rightPieces.add(piece);
        }
        int movesCount=0;
        for(Piece piece: rightPieces){
            List<Move>moves;
            if (piece instanceof Pawn) {
                moves = piece.getMoves(this);
            }
            else{
                moves = getPiecesMoves(piece.color).get(piece);
            }
            for (Move move: moves){
                if(makeAMoveSearch(move))
                {
                    int currentMoves = search(1 - color, depth - 1);
                    movesCount += currentMoves;
                    undoMove();
                }
            }
        }
        return movesCount;
    }
    public int search(int color,int depth){
        if (depth==0){
            return 1;
        }

        List<Move>moves=getMoves(color);
        int movesCount=0;
        for (Move move: moves){
            //for (int i = 0; i < 2 - depth; i++) {
            //    System.out.print("  ");
            //}
            //String moveString1=move.toString();
            //System.out.print(moveString1);
            //System.out.println(",");
            //if(moveString1.equals("c4d5") && depth==3){
            //    int a=6;
            //}
            //if (moveString1.equals("f8e8")){
            //    int a =5;
            //}
            if(makeAMoveSearch(move))
            {

                int currentMoves = search(1 - color, depth - 1);
                movesCount += currentMoves;
                undoMove();
                //if (depth==3){
                //    String moveString=move.toString();
                //    System.out.print(moveString);
                //    System.out.print(": ");
                //    System.out.println(currentMoves);
                //}


            }

        }

        return movesCount;
    }
    public void setupSearch(){
        attackedSquaresWhite=new ArrayList<Set<Piece>>();
        for (int i=0;i<64;i++){
            attackedSquaresWhite.add(new HashSet<Piece>());
        }
        attackedSquaresBlack=new ArrayList<Set<Piece>>();
        for (int i=0;i<64;i++){
            attackedSquaresBlack.add(new HashSet<Piece>());
        }

        pieceMovesWhite=new HashMap<>();
        for (Piece piece:pieces){
            if(piece.color==1){
                pieceMovesWhite.put(piece,new ArrayList<Move>());

            }
        }
        pieceMovesBlack=new HashMap<>();
        for (Piece piece:pieces){
            if(piece.color==0){
                pieceMovesBlack.put(piece,new ArrayList<Move>());

            }
        }
        for(Piece piece : pieces){
            List<Move>moves=piece.getPseudoMoves(this);
            List<Set<Piece>> attackedSquares=getAttackedSquares(piece.color);
            HashMap<Piece,List<Move>>pieceMoves=getPiecesMoves(piece.color);
            for(Move move : moves){
                attackedSquares.get(8*move.targetRow+ move.targetCol).add(piece);
                pieceMoves.get(piece).add(move);
            }
        }
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
    public boolean isInCheck2(int color){
        String kingInitial=color==0?"k":"K";
        Piece king=getPiece(kingInitial).get(0);
        return !getAttackedSquares(1-color).get(king.row*8+king.col).isEmpty();
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
        Piece target=getAt(move.targetRow,move.targetCol);
        if(target!=null && target.color==color){
            return false;
        }
        moveAPiece(move);
        //if(isInCheck(color)){
        if(isInCheck2(color)){

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
    private void recalculateAttacks(Piece piece){
        List<Set<Piece>> attackedSquares=getAttackedSquares(piece.color);
        List<Move> attacks=piece.getPseudoMoves(this);
        HashMap<Piece,List<Move>> piecesMoves=getPiecesMoves(piece.color);

        for (Move move : attacks) {
            attackedSquares.get(move.targetRow*8+move.targetCol).add(piece);
        }
        piecesMoves.put(piece,attacks);
    }
    private void removeOldAttacks(Piece piece){
        List<Set<Piece>> attackedSquares=getAttackedSquares(piece.color);


        List<Move> attacks=getPiecesMoves(piece.color).get(piece);

        for (Move move : attacks) {
            attackedSquares.get(move.targetRow*8+move.targetCol).remove(piece);

        }
    }
    private void recalculateOldPositions(Piece piece){
        removeOldAttacks(piece);
        for(Piece pieceToRecalculate:attackedSquaresWhite.get(piece.row*8+piece.col)) {
            recalculateAttacks(pieceToRecalculate);
        }
        for(Piece pieceToRecalculate:attackedSquaresBlack.get(piece.row*8+piece.col)) {
            recalculateAttacks(pieceToRecalculate);
        }
    }
    private boolean removeBlockedPositions(Piece piece,int blockedColor,int position){
        List<Set<Piece>> attackedSquares=getAttackedSquares(piece.color);
        List<Move> attacks=piece.getPseudoMoves(this);
        List<Move> oldAttacks=getPiecesMoves(piece.color).get(piece);
        HashMap<Piece,List<Move>> piecesMoves=getPiecesMoves(piece.color);
        boolean removePos=true;
        for(Move move:oldAttacks){
            if(position==move.targetRow*8+move.targetCol ){
                continue;
            }
            attackedSquares.get(move.targetRow*8+move.targetCol).remove(piece);
        }
        for(Move move:attacks){
            if (move.targetRow*8+move.targetCol==position){
                removePos=false;
            }
            attackedSquares.get(move.targetRow*8+move.targetCol).add(piece);
        }
        piecesMoves.put(piece,attacks);
        return removePos;
    }
    private void calculateNewPositions(Piece piece){
        List<Set<Piece>> attackedSquares=getAttackedSquares(piece.color);
        List<Set<Piece>> otherAttackedSquares=getAttackedSquares(1-piece.color);
        recalculateAttacks(piece);

        List<Piece>toRemove=new ArrayList<>();
        for(Piece pieceToRecalculate:attackedSquares.get(piece.row*8+piece.col)) {
            if(removeBlockedPositions(pieceToRecalculate,piece.color,piece.row*8+piece.col)){
                toRemove.add(piece);
            }
        }
        for(Piece pieceToRemove:toRemove){
            attackedSquares.get(piece.row*8+piece.col).remove(pieceToRemove);
        }

        toRemove=new ArrayList<>();
        for(Piece pieceToRecalculate:otherAttackedSquares.get(piece.row*8+piece.col)) {
            if(removeBlockedPositions(pieceToRecalculate,piece.color,piece.row*8+piece.col)){
                toRemove.add(piece);
            }
        }
        for(Piece pieceToRemove:toRemove){
            otherAttackedSquares.get(piece.row*8+piece.col).remove(pieceToRemove);
        }
    }
    private void moveAPieceWhenCastle(Move move){
        int kingColumn=4;
        Piece rook=getAt(move.initialRow,move.initialCol);
        Piece king=getAt(move.initialRow,kingColumn);
        board[move.initialRow][move.initialCol]=null;
        recalculateOldPositions(rook);//<-------
        recalculateOldPositions(king);//<-------
        rook.move(move.targetRow,move.targetCol);


        board[move.targetRow][move.targetCol]=rook;
        if (move.initialCol<king.col){
            king.move(king.row,king.col-2);
        }
        else{
            king.move(king.row,king.col+2);
        }
        board[king.row][king.col]=king;
        board[king.row][kingColumn]=null;

        calculateNewPositions(rook);//<---------------
        calculateNewPositions(king);//<---------------
        ReverseMove reverseMove=new ReverseMove(move.targetRow,move.targetCol,move.initialRow,move.initialCol,null,true,false,true,false);
        reverseMoves.push(reverseMove);
    }
    private void moveAPieceWhenEnPassant(Move move){
        Piece piece=getAt(move.initialRow,move.initialCol);
        board[move.initialRow][move.initialCol]=null;
        recalculateOldPositions(piece);//<-------

        board[move.targetRow][move.targetCol]=piece;
        piece.move(move.targetRow,move.targetCol);

        Piece takenPawn=getAt(move.initialRow,move.targetCol);
        pieces.remove(takenPawn);
        board[move.initialRow][move.targetCol]=null;

        removeOldAttacks(takenPawn);//<---------------
        calculateNewPositions(piece);//<---------------
        ReverseMove reverseMove=new ReverseMove(move.targetRow,move.targetCol,move.initialRow,move.initialCol,takenPawn,false,true,true,false);
        reverseMoves.push(reverseMove);
    }
    private void moveAPieceWhenPromotion(Move move){
        Piece piece=getAt(move.initialRow,move.initialCol);
        board[move.initialRow][move.initialCol]=null;
        recalculateOldPositions(piece);//<-------

        Piece oldPiece=board[move.targetRow][move.targetCol];
        if (oldPiece!=null){
            pieces.remove(oldPiece);
            removeOldAttacks(oldPiece);//<---------------
        }
        //board[move.targetRow][move.targetCol]=piece;
        //piece.move(move.targetRow,move.targetCol);
        Piece promotionPiece=pieceFactory(move.promotionPiece,move.targetRow,move.targetCol);
        promotionPiece.isMoved=true;
        pieces.remove(piece);
        pieces.add(promotionPiece);
        board[move.targetRow][move.targetCol]=promotionPiece;
        calculateNewPositions(promotionPiece);//<---------------
        ReverseMove reverseMove=new ReverseMove(move.targetRow,move.targetCol,move.initialRow,move.initialCol,oldPiece,false,false,false,true);
        reverseMoves.push(reverseMove);

    }
    private void moveAPiece(Move move){
        //TODO: Implement promotions
        //This method does not validate the move!
        if (move.isCastle){
            moveAPieceWhenCastle(move);
            return;
        }
        if(move.isEnPassant){
            moveAPieceWhenEnPassant(move);
            return;
        }
        if(move.isPromotion){
            moveAPieceWhenPromotion(move);
            return;
        }
        Piece piece=getAt(move.initialRow,move.initialCol);
        board[move.initialRow][move.initialCol]=null;
        recalculateOldPositions(piece);//<-------

        Piece oldPiece=board[move.targetRow][move.targetCol];
        if (oldPiece!=null){
            pieces.remove(oldPiece);
            removeOldAttacks(oldPiece);//<---------------
        }
        board[move.targetRow][move.targetCol]=piece;
        boolean isFirstMove=!piece.isMoved;
        piece.move(move.targetRow,move.targetCol);
        calculateNewPositions(piece);//<---------------
        ReverseMove reverseMove=new ReverseMove(move.targetRow,move.targetCol,move.initialRow,move.initialCol,oldPiece,false,false,isFirstMove,false);

        reverseMoves.push(reverseMove);
    }
    private void returnKingWhenUndoCastle(ReverseMove revMove){
        Piece king;
        if(revMove.targetCol==0){
            king = board[revMove.initialRow][2];
            board[revMove.initialRow][4]=king;
            board[revMove.initialRow][2]=null;
        }
        else{
            king = board[revMove.initialRow][6];
            board[revMove.initialRow][4]=king;
            board[revMove.initialRow][6]=null;
        }
        recalculateOldPositions(king);//<-------
        king.move(revMove.initialRow,4);
        king.isMoved=false;
        calculateNewPositions(king);//<----------------------------
    }
    private void returnPawnWhenUndoPromotion(ReverseMove revMove,Piece movedPiece){
        Piece oldPawn=pieceFactory(movedPiece.color==0?"p":"P",revMove.targetRow,revMove.targetCol);
        oldPawn.isMoved=true;
        removeOldAttacks(movedPiece);//<---------------
        pieces.remove(movedPiece);
        pieces.add(oldPawn);
        board[revMove.targetRow][revMove.targetCol]=oldPawn;
        calculateNewPositions(oldPawn);//<----------------------------

    }
    private void undoMove(){
        ReverseMove revMove=reverseMoves.pop();
        moveAPiece(new Move(revMove.initialRow,revMove.initialCol,revMove.targetRow,revMove.targetCol));
        reverseMoves.pop();
        Piece movedPiece=getAt(revMove.targetRow,revMove.targetCol);
        movedPiece.isMoved=!revMove.firstMove;
        if (revMove.takenPiece!=null){
            pieces.add(revMove.takenPiece);
            //board[revMove.initialRow][revMove.initialCol]=revMove.takenPiece;

        }
        if(revMove.isEnPassant){
            board[revMove.targetRow][revMove.initialCol]=revMove.takenPiece;
            return;
        }

        board[revMove.initialRow][revMove.initialCol]=revMove.takenPiece;

        if(revMove.takenPiece!=null){
            calculateNewPositions(revMove.takenPiece);//<----------------------------
        }
        if (revMove.isCastle){
            returnKingWhenUndoCastle(revMove);
            //recalculateOldPositions(movedPiece);
            //calculateNewPositions(movedPiece);//<----------------------------
        }
        if(revMove.isPromotion){
            returnPawnWhenUndoPromotion(revMove,movedPiece);
        }
    }
    public List<Piece> getPiece(String initial){
        if (initial.equals( "k")) {
            return Collections.singletonList(blackKing);
        }
        else if(initial.equals( "K")){
            return Collections.singletonList(whiteKing);

        }
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
        Board board=new Board();
        int row=0;
        int col=0;
        for (int i=0;i<fen.length();i++){
            if(fen.charAt(i)=='/'){
                row++;
                col=0;
            }
            else if(fen.charAt(i)<='9'){
                col+=fen.charAt(i)-'0';
            }
            else{
                Piece piece=pieceFactory(Character.toString((fen.charAt(i))),row,col);
                board.board[row][col]=piece;
                String initial=piece.getInitial();
                if(initial.equals("k")){
                    board.blackKing=piece;
                }
                if(initial.equals("K")){
                    board.whiteKing=piece;
                }
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
        piece.id=boardPieces;
        boardPieces++;
        return piece;
    }
}
