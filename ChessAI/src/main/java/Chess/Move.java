package Chess;

import Chess.Pieces.Base.Piece;

public class Move {
    public int initialRow;
    public int initialCol;
    public int targetRow;
    public int targetCol;
    public boolean isCastle=false;
    public boolean firstMove=false;
    public boolean isPromotion=false;
    public boolean isEnPassant=false;
    public String promotionPiece;
    public Move(int initialRow,int initialCol,int targetRow,int targetCol){
        this.initialRow=initialRow;
        this.initialCol=initialCol;
        this.targetRow=targetRow;
        this.targetCol=targetCol;
    }
    @Override
    public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }
        final Move other=(Move)obj;
        return other.isCastle==isCastle &&
                other.initialRow==initialRow &&
                other.initialCol==initialCol &&
                other.targetCol==targetCol &&
                other.targetRow==targetRow;
    }
    @Override
    public String toString(){
        String from=(char)('a'+initialCol)+Integer.toString( 8-initialRow);
        String to=(char)('a'+targetCol)+Integer.toString( 8-targetRow);
        String promotion=isPromotion?promotionPiece:"";
        return from+to+promotion;
    }
}
