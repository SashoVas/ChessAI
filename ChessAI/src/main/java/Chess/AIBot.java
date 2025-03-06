package Chess;

enum piece{
    wk(11),
    wq(1),
    wn(2),
    wb(4),
    wr(3),
    wp(9),
    bk(12),
    bq(5),
    bn(6),
    bb(8),
    br(7),
    bp(10);
    private final int value;

    piece(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
public class AIBot {
    public static int[]pieceValues={
        900,300,500,300,-900,-300,-500,-300,100,-100,0,0
    };
    public static int evaluateBoard(long board,int pieceType){
        int result=0;

        long current=board& -board;

        while(current!=0){
            result+=pieceValues[pieceType];
            board&=~current;
            current=board& -board;
        }
        return result;
    }
    public static int evaluate (long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,int color){
        int result=0;
        result+=evaluateBoard(wq,0);
        result+=evaluateBoard(wn,1);
        result+=evaluateBoard(wb,3);
        result+=evaluateBoard(wr,2);
        result+=evaluateBoard(wp,8);
        result+=evaluateBoard(bq,4);
        result+=evaluateBoard(bn,5);
        result+=evaluateBoard(bb,7);
        result+=evaluateBoard(br,6);
        result+=evaluateBoard(bp,9);

        return color==1?result:-result;
    }
}
