package Chess.Moves;

public class MoveUtilities {
    public static final int FIRST_FIVE_BITS=63;
    //promotion mappings
    //1-wq
    //2-wn
    //3-wr
    //4-wb
    //5-bq
    //6-bn
    //7-br
    //8-bb
    public static final int WQUEEN_MAPPING=1;
    public static final int WKNIGHT_MAPPING=2;
    public static final int WROOK_MAPPING=3;
    public static final int WBISHOP_MAPPING=4;
    public static final int BQUEEN_MAPPING=5;
    public static final int BKNIGHT_MAPPING=6;
    public static final int BROOK_MAPPING=7;
    public static final int BBISHOP_MAPPING=8;
    public static int codeMove(long startRow,long startCol,long endRow,long endCol,long promotionPiece,long isEnPassant,long isCastle){
        int startPiece=(int)(startRow*8+startCol);
        int endPiece=(int)(endRow*8+endCol);
        int result=0;
        result|=startPiece&FIRST_FIVE_BITS;
        result|=(endPiece&FIRST_FIVE_BITS)<<6;
        result|=isEnPassant<<12;
        result|=isCastle<<13;
        if(promotionPiece>0){
            result|=1<<14;
            result|=promotionPiece<<15;
        }
        return result;
    }

    public static boolean isPromotion(int code){
        return (code>>15&FIRST_FIVE_BITS)!=0;
    }
    public static boolean isEnPassant(int code){
        return ((code>>12)&1)!=0;
    }
    public static boolean isCastle(int code){
        return ((code>>13)&1)!=0;
    }
    public static int extractStart(int code){
        return code&FIRST_FIVE_BITS;
    }
    public static int extractEnd(int code){
        return (code>>6)&FIRST_FIVE_BITS;
    }
    public static int extractPromotion(int code){
        return (code>>15&FIRST_FIVE_BITS);
    }
}
