package Chess;

import java.util.List;
public class AIBot {
    public static final int[]pieceValues={
            0,900,300,300,500,100,0,-900,-300,-300,-500,-100
    };
    public static final int MOVE_TO_BE_SEARCHED=4;
    public static final  int WKING_INDEX=0;
    public static final  int WQUEEN_INDEX=1;
    public static final  int WKNIGHT_INDEX=2;
    public static final  int WBISHOP_INDEX=3;
    public static final  int WROOK_INDEX=4;
    public static final  int WPAWN_INDEX=5;
    public static final  int BKING_INDEX=6;
    public static final  int BQUEEN_INDEX=7;
    public static final  int BKNIGHT_INDEX=8;
    public static final  int BBISHOP_INDEX=9;
    public static final  int BROOK_INDEX=10;
    public static final  int BPAWN_INDEX=11;

    public static final int mvv_lva[][] = {
            {600, 500, 200, 300, 400, 100,  600, 500, 200, 300, 400, 100},
            {601, 501, 201, 301, 401, 101,  601, 501, 201, 301, 401, 101},
            {604, 504, 204, 304, 404, 104,  604, 504, 204, 304, 404, 104},
            {603, 503, 203, 303, 403, 103,  603, 503, 203, 303, 403, 103},
            {602, 502, 202, 302, 402, 102,  602, 502, 202, 302, 402, 102},
            {605, 505, 205, 305, 405, 105,  605, 505, 205, 305, 405, 105},

            {600, 500, 200, 300, 400, 100,  600, 500, 200, 300, 400, 100},
            {601, 501, 201, 301, 401, 101,  601, 501, 201, 301, 401, 101},
            {604, 504, 204, 304, 404, 104,  604, 504, 204, 304, 404, 104},
            {603, 503, 203, 303, 403, 103,  603, 503, 203, 303, 403, 103},
            {602, 502, 202, 302, 402, 102,  602, 502, 202, 302, 402, 102},
            {605, 505, 205, 305, 405, 105,  605, 505, 205, 305, 405, 105},
    };


    // pawn positional score
    public static final int pawn_score[] =
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
    public static final int knight_score[] =
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
    public static final int bishop_score[] =
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
    public static final int rook_score[] =
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
    public static final int king_score[] =
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
    public static long[][] killerMoves=new long[2][64];
    public static int[][] historyMoves=new int[12][64];

    public static int scoreMove(int move,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp){
        long targetIndex=MoveUtilities.extractFromCodedMove(move,2);
        int targetType=BitBoardMovesGenerator.getPieceType(targetIndex,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp);
        if(targetType!=-1){
            //score capture moves higher than others
            long startIndex=MoveUtilities.extractFromCodedMove(move,1);
            int startType=BitBoardMovesGenerator.getPieceType(startIndex,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp);

            return mvv_lva[startType][targetType]+ 10000;
        }
        else if(MoveUtilities.extractFromCodedMove(move,4)!=0 ){
            //score en passant as capture
            return mvv_lva[WPAWN_INDEX][WPAWN_INDEX] + 10000;
        }

        //score moves that are not captures, but produce cut ofs
        if(move==killerMoves[0][ply]){
            return 9000;
        }
        else if(move==killerMoves[1][ply]){
            return 8000;
        }
        //score moves that were good before
        long startIndex=MoveUtilities.extractFromCodedMove(move,1);
        int startType=BitBoardMovesGenerator.getPieceType(startIndex,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp);
        return historyMoves[startType][(int)targetIndex];
    }
    public static int evaluateBoard(long board,int pieceType){
        int result=0;

        long current=board& -board;

        while(current!=0){
            result+=pieceValues[pieceType];
            int index=Long.numberOfTrailingZeros(current);
            int blackPos=(7-index/8)*8+index%8;
            switch (pieceType){
                case WKNIGHT_INDEX:
                    result+=knight_score[index];
                    break;
                case WROOK_INDEX:
                    result+=rook_score[index];
                    break;
                case WBISHOP_INDEX:
                    result+=bishop_score[index];
                    break;
                case BKNIGHT_INDEX:
                    result-=knight_score[blackPos];
                    break;
                case BROOK_INDEX:
                    result-=rook_score[blackPos];
                    break;
                case BBISHOP_INDEX:
                    result-=bishop_score[blackPos];
                    break;
                case WPAWN_INDEX:
                    result+=pawn_score[index];
                    break;
                case BPAWN_INDEX:
                    result-=pawn_score[blackPos];
                    break;
                case WKING_INDEX:
                    result+=king_score[index];
                    break;
                case BKING_INDEX:
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
        result+=evaluateBoard(wq,WQUEEN_INDEX);
        result+=evaluateBoard(wn,WKNIGHT_INDEX);
        result+=evaluateBoard(wb,WBISHOP_INDEX);
        result+=evaluateBoard(wr,WROOK_INDEX);
        result+=evaluateBoard(wp,WPAWN_INDEX);
        result+=evaluateBoard(bq,BQUEEN_INDEX);
        result+=evaluateBoard(bn,BKNIGHT_INDEX);
        result+=evaluateBoard(bb,BBISHOP_INDEX);
        result+=evaluateBoard(br,BROOK_INDEX);
        result+=evaluateBoard(bp,BPAWN_INDEX);
        result+=evaluateBoard(wk,WKING_INDEX);
        result+=evaluateBoard(bk,BKING_INDEX);
        return color==1?result:-result;
    }
    public static int nodes=0;
    public static int bestMove=0;
    public static int ply=0;
    public static int quiescence(int alpha,int beta,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,boolean ckw,boolean cqw,boolean ckb,boolean cqb,int color,int lastMove){

        nodes++;
        //Prune
        int eval=evaluate(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,color);
        if(eval>=beta){
            return beta;
        }
        if(eval>alpha){
            alpha=eval;
        }

        long fullBoard=wk|wq|wn|wb|wr|wp|bk|bq|bn|bb|br|bp;
        List<Integer> moves;
        if(color==1){
            moves=BitBoardMovesGenerator.generateMovesW(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw,cqw,lastMove);
        }
        else{
            moves=BitBoardMovesGenerator.generateMovesB(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckb,cqb,lastMove);
        }

        moves.sort((a,b)-> Integer.compare(
                scoreMove(a,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp),
                scoreMove(b,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp))*-1);
        for(int move:moves){
            //Only captures
            long endPosition=MoveUtilities.extractFromCodedMove(move,2);
            if(((1L<<endPosition)&fullBoard)==0){
                //break;
                continue;
            }
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
            if(MoveUtilities.extractFromCodedMove(move,3)==0 && MoveUtilities.extractFromCodedMove(move,4)==0){
                //Castle
                long start=MoveUtilities.extractFromCodedMove(move,1);
                if(((1L<<start)&wk)!=0){ckwc=false;cqwc=false;}
                if(((1L<<start)&bk)!=0){ckbc=false;cqbc=false;}
                if(((1L<<start)&wr &(1L<<63))!=0){ckwc=false;}
                if(((1L<<start)&wr &(1L<<56))!=0){cqwc=false;}
                if(((1L<<start)&br &(1L<<7))!=0){ckbc=false;}
                if(((1L<<start)&br &(1L<<0))!=0){cqbc=false;}
            }
            ply++;
            int score=-quiescence(-beta,-alpha,wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc,ckwc,cqwc,ckbc,cqbc,1-color,move);
            ply--;

            //Prune
            if(score>=beta){
                return beta;
            }
            if(score>alpha){
                alpha=score;
            }
        }
        return alpha;
    }
    public static boolean lateMoveReductionCondition(int depth,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,int color,int move,int fullMovesSearched){
        long inCheck;
        long giveCheck;
        if(color==1){
            inCheck=BitBoardMovesGenerator.attackedByWhite(  wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp)&bk;
            giveCheck=BitBoardMovesGenerator.attackedByBlack(  wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp)&wk;
        }
        else{
            inCheck=BitBoardMovesGenerator.attackedByBlack(  wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp)&wk;
            giveCheck=BitBoardMovesGenerator.attackedByWhite(  wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp)&bk;
        }
        long endPosition=MoveUtilities.extractFromCodedMove(move,2);
        int endPosType=BitBoardMovesGenerator.getPieceType(endPosition,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp);
        return fullMovesSearched>MOVE_TO_BE_SEARCHED && depth>=3 && inCheck==0 && giveCheck==0 && move!=killerMoves[0][ply]&& move!=killerMoves[1][ply] && endPosType==-1 && MoveUtilities.extractFromCodedMove(move,3)==0;
    }
    public static boolean nullMovePruningCondition(int depth,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,int color){
        long inCheck;
        if(color==1){
            inCheck=BitBoardMovesGenerator.attackedByWhite(  wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp)&bk;
        }
        else{
            inCheck=BitBoardMovesGenerator.attackedByBlack(  wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp)&wk;
        }
        return inCheck==0 && depth>=3 && ply>0;
    }
    public static int negmax(int alpha,int beta,int depth,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,boolean ckw,boolean cqw,boolean ckb,boolean cqb,int color,int lastMove){

        if(depth==0){
            return quiescence(alpha,beta,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw,cqw,ckb,cqb,color,lastMove);
            //return evaluate(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,color);
        }
        nodes++;

        if(nullMovePruningCondition(depth,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,color)){
            //null move pruning
            int score=-negmax(-beta,-beta +1,depth-1 -2,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw,cqw,ckb,cqb,1-color,0);
            if(score>=beta){
                return beta;
            }
        }
        //Initialize possible moves
        List<Integer> moves;
        if(color==1){
            moves=BitBoardMovesGenerator.generateMovesW(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw,cqw,lastMove);
        }
        else{
            moves=BitBoardMovesGenerator.generateMovesB(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckb,cqb,lastMove);
        }
        if(moves.size()==0){
            return 0;
        }
        int bestCurrentMove=0;
        boolean isMate=true;

        int fullMovesSearched=0;
        //Sort The moves
        moves.sort((a,b)-> Integer.compare(
                scoreMove(a,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp),
                scoreMove(b,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp))*-1);
        //Check every move
        for(int move:moves){

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
            if(MoveUtilities.extractFromCodedMove(move,3)==0 && MoveUtilities.extractFromCodedMove(move,4)==0){
                //Castle
                long start=MoveUtilities.extractFromCodedMove(move,1);
                if(((1L<<start)&wk)!=0){ckwc=false;cqwc=false;}
                if(((1L<<start)&bk)!=0){ckbc=false;cqbc=false;}
                if(((1L<<start)&wr &(1L<<63))!=0){ckwc=false;}
                if(((1L<<start)&wr &(1L<<56))!=0){cqwc=false;}
                if(((1L<<start)&br &(1L<<7))!=0){ckbc=false;}
                if(((1L<<start)&br &(1L<<0))!=0){cqbc=false;}
            }

            isMate=false;
            ply++;
            int score;
            if(fullMovesSearched==0){
                score=-negmax(-beta,-alpha,depth-1,wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc,ckwc,cqwc,ckbc,cqbc,1-color,move);
            }
            else{
                //Late move reduction
                if(lateMoveReductionCondition(depth,wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc,color,move,fullMovesSearched)){
                    //PV search with late move reduction
                    score=-negmax(-alpha -1,-alpha,depth-2,wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc,ckwc,cqwc,ckbc,cqbc,1-color,move);
                }
                else{
                    score =alpha+1;
                }
                if(score>alpha){
                    //Only PV search, late move reduction does not work
                    score=-negmax(-alpha -1,-alpha,depth-1,wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc,ckwc,cqwc,ckbc,cqbc,1-color,move);
                    if(score> alpha && score<beta){
                        //PV and late move reduction does not work
                        score=-negmax(-beta,-alpha,depth-1,wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc,ckwc,cqwc,ckbc,cqbc,1-color,move);
                    }
                }
            }
            ply--;

            //Prune
            if(score>=beta){
                //Update killer move only if it is not capture
                long fullBoard=wk|wq|wn|wb|wr|wp|bk|bq|bn|bb|br|bp;
                long endPosition=MoveUtilities.extractFromCodedMove(move,2);
                if(((1L<<endPosition)&fullBoard)==0){
                    killerMoves[1][ply]=killerMoves[0][ply];
                    killerMoves[0][ply]=move;
                }

                return beta;
            }
            if(score>alpha){

                //Update history move, if it is not capture
                long fullBoard=wk|wq|wn|wb|wr|wp|bk|bq|bn|bb|br|bp;
                long endPosition=MoveUtilities.extractFromCodedMove(move,2);
                if(((1L<<endPosition)&fullBoard)==0){
                    int startPosType=BitBoardMovesGenerator.getPieceType(MoveUtilities.extractFromCodedMove(move,1),wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp);
                    historyMoves[startPosType][(int)endPosition]+=depth;
                }

                alpha=score;
                if(ply==0){
                    bestCurrentMove=move;
                }
            }
            fullMovesSearched++;
        }
        if(isMate){
            return -49000 +ply;
        }
        if (bestCurrentMove!=0){
            bestMove=bestCurrentMove;
        }
        return alpha;
    }

    public static int getBestMove(int depth,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,boolean ckw,boolean cqw,boolean ckb,boolean cqb,int color,int lastMove){
        ZobristHash.initializeHashes();
        nodes=0;
        bestMove=0;
        ply=0;

        int score=negmax(-50000,50000,depth,wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp,ckw,cqw,ckb,cqb,color,lastMove);
        System.out.println("Best score: "+score);
        return bestMove;
    }
}
