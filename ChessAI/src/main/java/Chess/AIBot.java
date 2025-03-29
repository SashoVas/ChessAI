package Chess;

import Chess.Evaluation.BoardEvaluation;
import Chess.Evaluation.MoveEvaluation;
import Chess.Moves.BitBoardMovesGenerator;
import Chess.Moves.MoveUtilities;
import Chess.TranspositionTable.TranspositionTable;
import Chess.TranspositionTable.ZobristHash;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AIBot {
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
    public static final int LOWER_BOUND_TYPE=1;
    public static final int UPPER_BOUND_TYPE=3;
    public static final int EXACT_BOUND_TYPE=2;
    public static final int INVALID_VALUE=-999999;
    public long hash=0L;
    public long lastHash=0L;
    public TranspositionTable tt=new TranspositionTable();
    public static int MAX_PLY=64;
    public static int MAX_MOVES_IN_GAME=1000;
    public int nodes=0;
    public int ply=0;
    public int historyPly=0;
    public long history[]=new long[MAX_MOVES_IN_GAME];
    public Set<Long> historySet=new HashSet<>(MAX_MOVES_IN_GAME);
    public MoveEvaluation moveEvaluation=new MoveEvaluation();
    public static final int INFINITY =50000;
    public static final int MATE_VAL=49000;
    public static final int MATE_SCORE=48000;
    public static final int MAX_EXTENSIONS=3;
    public int extensions=0;
    public static final int INVALID_MOVE=-700000;
    public int quiescence(int alpha,int beta,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,int color,int lastMove){

        nodes++;
        //Prune
        int eval= BoardEvaluation.evaluate(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,color);
        if(eval>=beta){
            return beta;
        }
        if(eval>alpha){
            alpha=eval;
        }

        List<Integer> moves;
        if(color==1)
            moves= BitBoardMovesGenerator.generateAttackMovesW(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,lastMove);

        else
            moves=BitBoardMovesGenerator.generateAttackMovesB(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,lastMove);


        moves.sort((a,b)-> Integer.compare(
                moveEvaluation.scoreMove(a,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,0,ply),
                moveEvaluation.scoreMove(b,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,0,ply))*-1);
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

            ply++;
            //historySet.add(hash);
            history[historyPly]=hash;
            historyPly++;
            int score=-quiescence(-beta,-alpha,wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc,1-color,move);
            ply--;
            historyPly--;
            //historySet.remove(hash);
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
    public boolean lateMoveReductionCondition(int depth,int move,int fullMovesSearched){
        return fullMovesSearched>=MOVE_TO_BE_SEARCHED && depth>=3   && !MoveUtilities.isPromotion(move);
    }
    public boolean detectRepetitions(){
        //return historySet.contains(hash);
        int to=Math.max(historyPly-5,0);
        for(int i=historyPly-1;i>=to;i--){
            if(history[i]==hash)
                return true;
        }
        return false;
    }
    public boolean nullMovePruning(int alpha,int beta,int depth,long wk, long wq, long wn, long wb, long wr, long wp, long bk, long bq, long bn, long bb, long br, long bp,boolean ckw,boolean cqw,boolean ckb,boolean cqb,int color,int lastMove){
        if(depth>=3 && ply>0){
            //null move pruning
            ply++;
            history[historyPly]=hash;
            historyPly++;

            //historySet.add(hash);
            //Hash side repeat
            hash^= ZobristHash.sideHash;
            //Remove en passant from hash if any
            hash=ZobristHash.hashEnPassantRights(hash,0,lastMove,wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp,color);
            int score=-negmax(-beta,-beta +1,depth-1 -2,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw,cqw,ckb,cqb,1-color,0);
            ply--;
            historyPly--;
            //historySet.remove(hash);
            if(score>=beta){
                return true;
            }
        }
        return false;
    }
    public int optimizedSearch(int move,int alpha,int beta,int depth,long wk, long wq, long wn, long wb, long wr, long wp, long bk, long bq, long bn, long bb, long br, long bp,boolean ckw,boolean cqw,boolean ckb,boolean cqb,int color,int lastMove,int fullMovesSearched){
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
        boolean whiteCheck=(BitBoardMovesGenerator.attackedByBlack(  wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc)&wkc)!=0;
        boolean blackCheck=(BitBoardMovesGenerator.attackedByWhite(  wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc)&bkc)!=0;
        if((color==1 && whiteCheck)||
                (color==0&& blackCheck)){
            return INVALID_MOVE;
        }

        //Update castle rules
        boolean ckwc=ckw;
        boolean cqwc=cqw;
        boolean ckbc=ckb;
        boolean cqbc=cqb;
        if(!MoveUtilities.isPromotion(move) && !MoveUtilities.isEnPassant(move)){
            //Castle
            long start=MoveUtilities.extractStart(move);
            if(((1L<<start)&wk)!=0){ckwc=false;cqwc=false;}
            if(((1L<<start)&bk)!=0){ckbc=false;cqbc=false;}
            if(((1L<<start)&wr &(1L<<63))!=0){ckwc=false;}
            if(((1L<<start)&wr &(1L<<56))!=0){cqwc=false;}
            if(((1L<<start)&br &(1L<<7))!=0){ckbc=false;}
            if(((1L<<start)&br &(1L<<0))!=0){cqbc=false;}
        }

        hash=ZobristHash.hashMove(hash,move,lastMove,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw,cqw,ckb,cqb,ckwc,cqwc,ckbc,cqbc,color);
        if(ply!=0 && detectRepetitions())
            return 0;
        ply++;
        history[historyPly]=hash;
        historyPly++;
        //historySet.add(hash);
        int score;
        //check extensions
        boolean inExtencion=false;
        if( extensions<MAX_EXTENSIONS &&((color==1 && blackCheck)||(color==0 && whiteCheck))){
            depth++;
            extensions++;
            inExtencion=true;
        }
        if(fullMovesSearched==0){
            score=-negmax(-beta,-alpha,depth-1,wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc,ckwc,cqwc,ckbc,cqbc,1-color,move);
        }
        else{
            long endPosition=MoveUtilities.extractEnd(move);
            int endPosType=BitBoardMovesGenerator.getPieceType(endPosition,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp);
            //Late move reduction
            if( endPosType==-1  && lateMoveReductionCondition(depth,move,fullMovesSearched)){
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
        historyPly--;
        //historySet.remove(hash);
        //re increment extensions
        if( inExtencion) {
            extensions--;
            depth--;
        }
        return score;
    }
    public static int pastBestMove=0;
    public int negmax(int alpha,int beta,int depth,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,boolean ckw,boolean cqw,boolean ckb,boolean cqb,int color,int lastMove){

        moveEvaluation.pvLength[ply]=ply;
        //Check if current move is repeated(helps avoid 3-fold repetition)
        //if(ply!=0 && detectRepetitions())
        //    return 0;

        boolean pvNode = (beta - alpha) > 1;

        //if the move was processed before, use the old score  7699081 6727348 7151907 3053515 1803879 1514321
        int currentBestMove;
        if(ply>0 && !pvNode  && tt.containsKey(hash)){
            int res= tt.retrieveFromTable(hash,depth,alpha,beta,ply);
            if(res!=INVALID_VALUE)
                return res;
            currentBestMove=pastBestMove;
        } else
            currentBestMove = 0;

        //Search the captures, so that we don't blunder pieces
        if(depth==0)
            return quiescence(alpha,beta,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,color,lastMove);

        nodes++;

        boolean inCheck;
        if(color==1){
            inCheck=(BitBoardMovesGenerator.attackedByBlack(  wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp)&wk)!=0;
        }
        else{
            inCheck=(BitBoardMovesGenerator.attackedByWhite(  wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp)&bk)!=0;
        }
        int staticEval= BoardEvaluation.evaluate(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,color);
        if (depth < 3 && !pvNode && !inCheck  &&  Math.abs(beta - 1) > -INFINITY + 100)
        {
            // define evaluation margin
            int evalMargin = 120 * depth;
            // evaluation margin substracted from static evaluation score fails high
            if (staticEval - evalMargin >= beta)
                // evaluation margin substracted from static evaluation score
                return staticEval - evalMargin;
        }

        long oldHash=hash;
        int bestMove=0;
        //null move pruning
        if(!inCheck && nullMovePruning(alpha,beta,depth,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw,cqw,ckb,cqb,color,lastMove)) {
            hash= oldHash;
            return beta;
        }

        //razoring 9237667 66684248 11813067
        int score;
        if (!inCheck && depth <= 3)
        {
            score = staticEval + 125;
            int new_score;
            if (score < beta)
            {
                if (depth == 1)
                {
                    new_score = quiescence(alpha,beta,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,color,lastMove);
                    return (new_score > score) ? new_score : score;
                }
                score += 175;
                if (score < beta && depth <= 2)
                {
                    new_score = quiescence(alpha,beta,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,color,lastMove);
                    if (new_score < beta)
                        return (new_score > score) ? new_score : score;
                }
            }
        }


        hash= oldHash;
        //Initialize possible moves
        List<Integer> moves;
        if(color==1)
            moves=BitBoardMovesGenerator.generateMovesW(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw,cqw,lastMove);
        else
            moves=BitBoardMovesGenerator.generateMovesB(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckb,cqb,lastMove);
        if(moves.size()==0)
            return 0;

        boolean isMate=true;
        int fullMovesSearched=0;
        moveEvaluation.scorePv=false;
        //Sort The moves, so that we check the strong moves first and prune the week moves later
        moves.sort((a, b)-> Integer.compare(
                moveEvaluation.scoreMove(a,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp, currentBestMove,ply),
                moveEvaluation.scoreMove(b,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp, currentBestMove,ply))*-1);


        moveEvaluation.followPv=moveEvaluation.scorePv;
        moveEvaluation.scorePv=false;
        //variable that indicate what type of node the current one is(used in transposition table)
        int nodeType=LOWER_BOUND_TYPE;
        //Check every move
        for(int move:moves){

            //Search the move, and get its optimal score
            score=optimizedSearch(move,alpha,beta,depth,wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp,ckw,cqw,ckb,cqb,color,lastMove,fullMovesSearched);
            hash=oldHash;
            //the move is invalid
            if(score==INVALID_MOVE){
                continue;
            }
            isMate=false;

            if(score>alpha){

                //Update history move, if it is not capture
                long fullBoard=wk|wq|wn|wb|wr|wp|bk|bq|bn|bb|br|bp;
                long endPosition=MoveUtilities.extractEnd(move);
                if(((1L<<endPosition)&fullBoard)==0){
                    int startPosType=BitBoardMovesGenerator.getPieceType(MoveUtilities.extractStart(move),wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp);
                    moveEvaluation.historyMoves[startPosType][(int)endPosition]+=depth;
                }
                nodeType=EXACT_BOUND_TYPE;
                alpha=score;
                bestMove=move;
                //Add pv moves to table
                moveEvaluation.pvTable[ply][ply]=move;
                for(int nextPly=ply+1;nextPly<moveEvaluation.pvLength[ply+1];nextPly++)
                    moveEvaluation.pvTable[ply][nextPly]=moveEvaluation.pvTable[ply+1][nextPly];
                moveEvaluation.pvLength[ply]=moveEvaluation.pvLength[ply+1];
                //Prune
                if(score>=beta){
                    //Update killer move only if it is not capture
                    if(((1L<<endPosition)&fullBoard)==0){
                        moveEvaluation.killerMoves[1][ply]=moveEvaluation.killerMoves[0][ply];
                        moveEvaluation.killerMoves[0][ply]=move;
                    }
                    tt.addToTable(hash,beta,depth,UPPER_BOUND_TYPE,bestMove,ply);
                    return beta;
                }
            }
            fullMovesSearched++;

        }
        if(isMate){
            //Check if, im in check, or stalemate
            if((color==1 && (BitBoardMovesGenerator.attackedByBlack(  wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp)&wk)!=0)||
                    (color==0&& (BitBoardMovesGenerator.attackedByWhite(  wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp)&bk)!=0)){
                return -MATE_VAL +ply;
            }
            return 0;
        }
        tt.addToTable(hash,alpha,depth,nodeType,bestMove,ply);
        return alpha;
    }
    public int getBestMoveIterativeDeepening(int depth,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,boolean ckw,boolean cqw,boolean ckb,boolean cqb,int color,int lastMove){
        //ZobristHash.initializeHashes();
        nodes=0;
        ply=0;
        int alphaIncrement=50;
        int bettaIncrement=50;
        int alpha=-INFINITY;
        int beta=INFINITY;
        int score=0;
        moveEvaluation.followPv=false;
        moveEvaluation.scorePv=false;
        //tt.clear();
        tt=new TranspositionTable();
        moveEvaluation.pvLength=new int[MAX_PLY];
        moveEvaluation.pvTable=new int[MAX_PLY][64];
        moveEvaluation.killerMoves=new long[2][64];
        moveEvaluation.historyMoves=new int[12][64];
        for(int current_depth=1;current_depth<=depth;current_depth++){
            //nodes=0;
            moveEvaluation.followPv=true;
            extensions=0;
            score=negmax(alpha,beta,current_depth,wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp,ckw,cqw,ckb,cqb,color,lastMove);
            if ((score <= alpha) || (score >= beta)) {
                alpha = -INFINITY;
                beta = INFINITY;
                current_depth--;
                continue;
            }
            alpha = score - alphaIncrement;
            beta = score + bettaIncrement;

            System.out.println("depth "+current_depth+","+"Nodes: "+nodes+","+"score: "+score);
            System.out.print("PVs: ");
            for(int i=0;i<moveEvaluation.pvLength[0];i++){

                System.out.print(BitBoard.toAlgebra(moveEvaluation.pvTable[0][i])+", ");
            }
            System.out.println();
        }

        System.out.println("Best score: "+score);
        return moveEvaluation.pvTable[0][0];
    }
    public int getBestMove(int depth,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,boolean ckw,boolean cqw,boolean ckb,boolean cqb,int color,int lastMove){
        //ZobristHash.initializeHashes();
        nodes=0;
        ply=0;
        moveEvaluation.scorePv=false;
        moveEvaluation.followPv=false;
        int score=negmax(-50000,50000,depth,wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp,ckw,cqw,ckb,cqb,color,lastMove);
        System.out.println("Best score: "+score);
        System.out.print("PVs: ");
        for(int i=0;i<moveEvaluation.pvLength[0];i++){

            System.out.print(BitBoard.toAlgebra(moveEvaluation.pvTable[0][i])+", ");
        }
        System.out.println();
        return moveEvaluation.pvTable[0][0];
    }

}
