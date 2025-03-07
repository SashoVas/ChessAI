package Chess;

import java.util.List;

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
    // pawn positional score
    public static int pawn_score[] =
            {
                    90,  90,  90,  90,  90,  90,  90,  90,
                    30,  30,  30,  40,  40,  30,  30,  30,
                    20,  20,  20,  30,  30,  30,  20,  20,
                    10,  10,  10,  20,  20,  10,  10,  10,
                    5,   5,  10,  20,  20,   5,   5,   5,
                    0,   0,   0,   5,   5,   0,   0,   0,
                    0,   0,   0, -10, -10,   0,   0,   0,
                    0,   0,   0,   0,   0,   0,   0,   0
            };

    // knight positional score
    public static int knight_score[] =
            {
                    -5,   0,   0,   0,   0,   0,   0,  -5,
                    -5,   0,   0,  10,  10,   0,   0,  -5,
                    -5,   5,  20,  20,  20,  20,   5,  -5,
                    -5,  10,  20,  30,  30,  20,  10,  -5,
                    -5,  10,  20,  30,  30,  20,  10,  -5,
                    -5,   5,  20,  10,  10,  20,   5,  -5,
                    -5,   0,   0,   0,   0,   0,   0,  -5,
                    -5, -10,   0,   0,   0,   0, -10,  -5
            };

    // bishop positional score
    public static int bishop_score[] =
            {
                    0,   0,   0,   0,   0,   0,   0,   0,
                    0,   0,   0,   0,   0,   0,   0,   0,
                    0,   0,   0,  10,  10,   0,   0,   0,
                    0,   0,  10,  20,  20,  10,   0,   0,
                    0,   0,  10,  20,  20,  10,   0,   0,
                    0,  10,   0,   0,   0,   0,  10,   0,
                    0,  30,   0,   0,   0,   0,  30,   0,
                    0,   0, -10,   0,   0, -10,   0,   0

            };

    // rook positional score
    public static int rook_score[] =
            {
                    50,  50,  50,  50,  50,  50,  50,  50,
                    50,  50,  50,  50,  50,  50,  50,  50,
                    0,   0,  10,  20,  20,  10,   0,   0,
                    0,   0,  10,  20,  20,  10,   0,   0,
                    0,   0,  10,  20,  20,  10,   0,   0,
                    0,   0,  10,  20,  20,  10,   0,   0,
                    0,   0,  10,  20,  20,  10,   0,   0,
                    0,   0,   0,  20,  20,   0,   0,   0

            };

    // king positional score
    public static int king_score[] =
            {
                    0,   0,   0,   0,   0,   0,   0,   0,
                    0,   0,   5,   5,   5,   5,   0,   0,
                    0,   5,   5,  10,  10,   5,   5,   0,
                    0,   5,  10,  20,  20,  10,   5,   0,
                    0,   5,  10,  20,  20,  10,   5,   0,
                    0,   0,   5,  10,  10,   5,   0,   0,
                    0,   5,   5,  -5,  -5,   0,   5,   0,
                    0,   0,   5,   0, -15,   0,  10,   0
            };
    public static int evaluateBoard(long board,int pieceType){
        int result=0;

        long current=board& -board;

        while(current!=0){
            result+=pieceValues[pieceType];
            int index=Long.numberOfTrailingZeros(current);
            int blackPos=(7-index/8)*8+index%8;
            switch (pieceType){
                case 1:
                    result+=knight_score[index];
                    break;
                case 2:
                    result+=rook_score[index];
                    break;
                case 3:
                    result+=bishop_score[index];
                    break;
                case 5:
                    result-=knight_score[blackPos];
                    break;
                case 6:
                    result-=rook_score[blackPos];
                    break;
                case 7:
                    result-=bishop_score[blackPos];
                    break;
                case 8:
                    result+=pawn_score[index];
                    break;
                case 9:
                    result-=pawn_score[blackPos];
                    break;
                case 10:
                    result+=king_score[index];
                    break;
                case 11:
                    result-=king_score[blackPos];
                    break;
                default:
                    break;
            }
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
        result+=evaluateBoard(wk,10);
        result+=evaluateBoard(bk,11);
        return color==1?result:-result;
    }
    public static int nodes=0;
    public static long bestMove=0;
    public static int ply=0;
    public static int negmax(int alpha,int beta,int depth,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,boolean ckw,boolean cqw,boolean ckb,boolean cqb,int color,long lastMove){

        if(depth==0){
            return evaluate(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,color);
        }
        nodes++;

        //Initialize possible moves
        List<Long> moves;
        if(color==1){
            moves=BitBoardMovesGenerator.generateMovesW(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw,cqw,lastMove);
        }
        else{
            moves=BitBoardMovesGenerator.generateMovesB(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckb,cqb,lastMove);
        }

        int score=0;
        long bestCurrentMove=0;
        //Check every move
        for(long move:moves){
            //Make the move
            long wkc=BitBoardMovesGenerator.makeAMoveOnBoard(wk,move,11);
            long wqc=BitBoardMovesGenerator.makeAMoveOnBoard(wq,move,1);
            long wnc=BitBoardMovesGenerator.makeAMoveOnBoard(wn,move,2);
            long wbc=BitBoardMovesGenerator.makeAMoveOnBoard(wb,move,4);
            long wrc=BitBoardMovesGenerator.makeAMoveOnBoard(wr,move,3);
            long wpc=BitBoardMovesGenerator.makeAMoveOnBoard(wp,move,9);
            long bkc=BitBoardMovesGenerator.makeAMoveOnBoard(bk,move,12);
            long bqc=BitBoardMovesGenerator.makeAMoveOnBoard(bq,move,5);
            long bnc=BitBoardMovesGenerator.makeAMoveOnBoard(bn,move,6);
            long bbc=BitBoardMovesGenerator.makeAMoveOnBoard(bb,move,8);
            long brc=BitBoardMovesGenerator.makeAMoveOnBoard(br,move,7);
            long bpc=BitBoardMovesGenerator.makeAMoveOnBoard(bp,move,10);

            //Check if move is legal
            if((color==1 && ((BitBoardMovesGenerator.attackedByBlack(  wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc)&wkc)!=0))||
                    (color==0&& ((BitBoardMovesGenerator.attackedByWhite(  wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc)&bkc)!=0))){
                continue;
            }

            //Update castle rules
            boolean ckwc=ckw;
            boolean cqwc=cqw;
            boolean ckbc=ckb;
            boolean cqbc=cqb;
            if(move<10000 || move>=1000000){
                //Castle
                long start=BitBoardMovesGenerator.extractFromCodedMove(move,1);
                if(((1L<<start)&wk)!=0){ckwc=false;cqwc=false;}
                if(((1L<<start)&bk)!=0){ckbc=false;cqbc=false;}
                if(((1L<<start)&wr &(1L<<63))!=0){ckwc=false;}
                if(((1L<<start)&wr &(1L<<56))!=0){cqwc=false;}
                if(((1L<<start)&br &(1L<<7))!=0){ckbc=false;}
                if(((1L<<start)&br &(1L<<0))!=0){cqbc=false;}
            }
            ply++;
            score=-negmax(-beta,-alpha,depth-1,wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc,ckwc,cqwc,ckbc,cqbc,1-color,move);
            ply--;
            if(score>=beta){
                return beta;
            }
            if(score>alpha){
                alpha=score;
                if(ply==0){
                    bestCurrentMove=move;

                }
            }
        }
        if (bestCurrentMove!=0){
            bestMove=bestCurrentMove;
        }
        return alpha;
    }

    public static long getBestMove(int depth,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,boolean ckw,boolean cqw,boolean ckb,boolean cqb,int color,long lastMove){
        int score=negmax(-50000,50000,depth,wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp,ckw,cqw,ckb,cqb,color,lastMove);
        System.out.println("Best score: "+score);
        return bestMove;
    }
}
