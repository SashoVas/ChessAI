package Chess;

import Chess.Pieces.Base.Piece;

public class ReverseMove extends Move{

    Piece takenPiece;

    public ReverseMove(int initialRow, int initialCol, int targetRow, int targetCol,Piece takenPiece,boolean isCastle,boolean isEnPassant,boolean firstMove) {
        super(initialRow, initialCol, targetRow, targetCol);
        this.takenPiece=takenPiece;
        this.isCastle=isCastle;
        this.isEnPassant=isEnPassant;
        this.firstMove=firstMove;
    }
}
