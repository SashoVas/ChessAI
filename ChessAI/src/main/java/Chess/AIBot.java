package Chess;

import Chess.TranspositionTable.TranspositionTable;

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
    public static final int lowerBoundType=1;
    public static final int upperBoundType=3;
    public static final int exactBoundType=2;
    public static final int invalidValue=-999999;

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
                    0,   0,   0, -15, -15,   0,   0,   0,
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
    public static long hash=0L;
    public static TranspositionTable tt=new TranspositionTable();
    public static int MAX_PLY=64;
    public static int pvLength[]=new int[MAX_PLY];
    public static int pvTable[][]=new int[MAX_PLY][64];
    public static int MAX_MOVES_IN_GAME=1000;
    public static boolean followPv=false;
    public static boolean scorePv=false;
    public static int nodes=0;
    public static int ply=0;
    public static int historyPly=0;
    public static long history[]=new long[MAX_MOVES_IN_GAME];
    public static final int infinity=50000;
    public static final int mateVal=49000;
    public static final int mateScore=48000;
    public static long[] isolatedPawnMasks=new long[8];
    public static long[] passedPawnMasksWhite=new long[64];
    public static long[] passedPawnMasksBlack=new long[64];
    public static final int doublePawnPenalty=-10;
    public static final int isolatedPawnPenalty=-10;
    public static final int[] passedPawnsBonus={0, 10, 30, 50, 75, 100, 150, 200};
    public static final int semiOpenFileScore=10;
    public static final int openFileScore=15;
    public static final int kingSafetyBonus=10;
    public static final int bishopMobilityBonus=5;
    public static int getRookScore(int rookIndex,long pawns,long enemyPawns){
        int score=0;
        if((pawns & BitBoardMovesGenerator.FILE_MASKS[rookIndex%8])==0)
            score+=semiOpenFileScore;
        if(((pawns|enemyPawns) & BitBoardMovesGenerator.FILE_MASKS[rookIndex%8])==0)
            score += openFileScore;
        return score;
    }
    public static int getKingScore(int kingIndex,long pawns,long enemyPawns){
        int score=0;
        if((pawns & BitBoardMovesGenerator.FILE_MASKS[kingIndex%8])==0)
            score-=semiOpenFileScore;
        if(((pawns|enemyPawns) & BitBoardMovesGenerator.FILE_MASKS[kingIndex%8])==0)
            score-=openFileScore;

        long moves=0;
        if(kingIndex>9){
            moves=BitBoardMovesGenerator.KING_MOVES_MASK<<(kingIndex-9);
        }
        else{
            moves=BitBoardMovesGenerator.KING_MOVES_MASK>>(9-kingIndex);
        }
        if(kingIndex%8<4){
            moves&=~(BitBoardMovesGenerator.FILE_MASKS[6]|BitBoardMovesGenerator.FILE_MASKS[7]);
        }
        else{
            moves&=~(BitBoardMovesGenerator.FILE_MASKS[0]|BitBoardMovesGenerator.FILE_MASKS[1]);
        }
        score+=BitBoardMovesGenerator.countBits(moves&pawns)*kingSafetyBonus;
        return score;
    }

    public static int getPawnsScore(long pawns,int index,long enemyPawns,int color){
        int score=0;

        //double pawns
        long stackedPawnMask=pawns & BitBoardMovesGenerator.FILE_MASKS[index%8];
        int bitsCount=BitBoardMovesGenerator.countBits(stackedPawnMask);
        if(bitsCount>1)
            score+=doublePawnPenalty*bitsCount;

        //isolated pawns
        if((pawns & isolatedPawnMasks[index%8]) ==0)
            score+=isolatedPawnPenalty;

        //passed pawns
        //BitBoardMovesGenerator.printMask(passedPawnMasksWhite[index]);
        if(color==1 && (passedPawnMasksWhite[index]&enemyPawns)==0){
            score+=passedPawnsBonus[7-index/8];
        }
        else if(color==0 && (passedPawnMasksBlack[index]&enemyPawns)==0){
            score+=passedPawnsBonus[index/8];
        }
        return score;
    }

    public static void generatePawnMasks(){
        //generates the masks for isolated pawns in specific file by or-ing the files to the left and right of it
        for(int i=1;i<7;i++){
            isolatedPawnMasks[i]=BitBoardMovesGenerator.FILE_MASKS[i-1]|BitBoardMovesGenerator.FILE_MASKS[i+1];
        }
        isolatedPawnMasks[0]=BitBoardMovesGenerator.FILE_MASKS[1];
        isolatedPawnMasks[7]=BitBoardMovesGenerator.FILE_MASKS[6];

        //generation of black pass pawns masks
        passedPawnMasksBlack[1]=BitBoardMovesGenerator.FILE_MASKS[0]|BitBoardMovesGenerator.FILE_MASKS[1]|BitBoardMovesGenerator.FILE_MASKS[2];
        passedPawnMasksBlack[1]&=~(BitBoardMovesGenerator.RANK_MASKS[7]);
        passedPawnMasksBlack[0]=BitBoardMovesGenerator.FILE_MASKS[0]|BitBoardMovesGenerator.FILE_MASKS[1];
        passedPawnMasksBlack[0]&=~(BitBoardMovesGenerator.RANK_MASKS[7]);
        passedPawnMasksBlack[7]=BitBoardMovesGenerator.FILE_MASKS[7]|BitBoardMovesGenerator.FILE_MASKS[6];
        passedPawnMasksBlack[7]&=~(BitBoardMovesGenerator.RANK_MASKS[7]);
        for(int i=1;i<7;i++){
            passedPawnMasksBlack[i]=passedPawnMasksBlack[1]<<(i-1);
        }
        for(int i=8;i<64;i++){
            passedPawnMasksBlack[i]=passedPawnMasksBlack[i-8]&(~BitBoardMovesGenerator.RANK_MASKS[7-(i/8)]);
        }

        //generation of white pass pawns masks
        passedPawnMasksWhite[1]=BitBoardMovesGenerator.FILE_MASKS[0]|BitBoardMovesGenerator.FILE_MASKS[1]|BitBoardMovesGenerator.FILE_MASKS[2];
        passedPawnMasksWhite[1]&=~(BitBoardMovesGenerator.RANK_MASKS[0]);
        passedPawnMasksWhite[0]=BitBoardMovesGenerator.FILE_MASKS[0]|BitBoardMovesGenerator.FILE_MASKS[1];
        passedPawnMasksWhite[0]&=~(BitBoardMovesGenerator.RANK_MASKS[0]);
        passedPawnMasksWhite[7]=BitBoardMovesGenerator.FILE_MASKS[7]|BitBoardMovesGenerator.FILE_MASKS[6];
        passedPawnMasksWhite[7]&=~(BitBoardMovesGenerator.RANK_MASKS[0]);
        for(int i=1;i<7;i++){
            passedPawnMasksWhite[i]=passedPawnMasksWhite[1]<<(i-1);
        }
        for(int i=8;i<64;i++){
            passedPawnMasksWhite[i]=passedPawnMasksWhite[i-8]&(~BitBoardMovesGenerator.RANK_MASKS[(i/8)]);
        }

        //swap orientation

        for(int i=0;i<4;i++){
            for(int j=0;j<8;j++){
                long temp=passedPawnMasksWhite[i*8+j];
                passedPawnMasksWhite[i*8+j]=passedPawnMasksWhite[56-i*8 +j];
                passedPawnMasksWhite[56-i*8 +j]=temp;
            }

        }
    }

    public static int scoreMove(int move,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp){
        long targetIndex=MoveUtilities.extractFromCodedMove(move,2);
        int targetType=BitBoardMovesGenerator.getPieceType(targetIndex,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp);

        if(followPv && pvTable[0][ply]==move){
            scorePv=true;
            return 20000;
        }
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
        //score castle and promotions higher
        if(MoveUtilities.extractFromCodedMove(move,5)!=0 ||MoveUtilities.extractFromCodedMove(move,3)!=0){
            return 5000;
        }
        //score moves that were good before
        long startIndex=MoveUtilities.extractFromCodedMove(move,1);
        int startType=BitBoardMovesGenerator.getPieceType(startIndex,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp);
        return historyMoves[startType][(int)targetIndex];
    }
    public static int evaluatePawnBoard(long board,int pieceType,long enemyPawns){
        int result=0;
        long fullBoard=board;
        long current=board& -board;

        while(current!=0){
            result+=pieceValues[pieceType];
            int index=Long.numberOfTrailingZeros(current);
            int blackPos=(7-index/8)*8+index%8;
            switch (pieceType){
                case WPAWN_INDEX:
                    result+=pawn_score[index];
                    result+=getPawnsScore(fullBoard,index,enemyPawns,1);
                    break;
                case BPAWN_INDEX:
                    result-=pawn_score[blackPos];
                    result-=getPawnsScore(fullBoard,index,enemyPawns,0);
                    break;
            }
            board&=~current;
            current=board& -board;
        }
        return result;
    }
    public static int evaluateRookBoard(long board,int pieceType,long pawns,long enemyPawns){
        int result=0;
        long current=board& -board;

        while(current!=0){
            result+=pieceValues[pieceType];
            int index=Long.numberOfTrailingZeros(current);
            int blackPos=(7-index/8)*8+index%8;
            switch (pieceType){
                case WROOK_INDEX:
                    result+=rook_score[index];
                    result+=getRookScore(index,pawns,enemyPawns);
                    break;
                case BROOK_INDEX:
                    result-=rook_score[blackPos];
                    result-=getRookScore(index,pawns,enemyPawns);
                    break;
            }
            board&=~current;
            current=board& -board;
        }
        return result;
    }
    public static int evaluationKingBoard(long board,int pieceType,long pawns,long enemyPawns){
        int result=0;
        long current=board& -board;

        while(current!=0){
            result+=pieceValues[pieceType];
            int index=Long.numberOfTrailingZeros(current);
            int blackPos=(7-index/8)*8+index%8;
            switch (pieceType){
                case WKING_INDEX:
                    result+=king_score[index];
                    result+=getKingScore(index,pawns,enemyPawns);
                    break;
                case BKING_INDEX:
                    result-=king_score[blackPos];
                    result-=getKingScore(index,pawns,enemyPawns);
                    break;
            }
            board&=~current;
            current=board& -board;
        }
        return result;
    }
    public static int evaluateBishopMobilityBonus(int bishopIndex,long occupied,long toTakeAndEmpty){
        long moves=BitBoardMovesGenerator.generateDiagonalMoves(bishopIndex,occupied) & toTakeAndEmpty;
        return bishopMobilityBonus* BitBoardMovesGenerator.countBits(moves);
    }
    public static int evaluateBishopBoard(long board,int pieceType,long occupied,long toTakeAndEmpty){
        int result=0;
        long current=board& -board;

        while(current!=0){
            result+=pieceValues[pieceType];
            int index=Long.numberOfTrailingZeros(current);
            int blackPos=(7-index/8)*8+index%8;
            switch (pieceType){
                case WBISHOP_INDEX:
                    result+=bishop_score[index];
                    result+=evaluateBishopMobilityBonus(index,occupied,toTakeAndEmpty);
                    break;
                case BBISHOP_INDEX:
                    result-=bishop_score[blackPos];
                    result-=evaluateBishopMobilityBonus(index,occupied,toTakeAndEmpty);

                    break;
            }
            board&=~current;
            current=board& -board;
        }
        return result;
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
                    //result+=getPawnsScore(fullBoard,index);
                    break;
                case BPAWN_INDEX:
                    result-=pawn_score[blackPos];
                    //result-=getPawnsScore(fullBoard,index);
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
        return Evaluation.evaluate(wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp,color);
    }
    public static int evaluateOld (long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,int color){
        long white=wk| wq| wn| wb| wr| wp;
        long black=bk| bq| bn| bb| br| bp;
        long occupied=black|white;
        int result=0;
        // evaluation of non pawn boards
        result+=evaluateBoard(wq,WQUEEN_INDEX);
        result+=evaluateBoard(wn,WKNIGHT_INDEX);
        //result+=evaluateBoard(wb,WBISHOP_INDEX);
        //result+=evaluateBoard(wr,WROOK_INDEX);
        //result+=evaluateBoard(wp,WPAWN_INDEX);
        result+=evaluateBoard(bq,BQUEEN_INDEX);
        result+=evaluateBoard(bn,BKNIGHT_INDEX);
        //result+=evaluateBoard(bb,BBISHOP_INDEX);
        //result+=evaluateBoard(br,BROOK_INDEX);
        //result+=evaluateBoard(bp,BPAWN_INDEX);
        //result+=evaluateBoard(wk,WKING_INDEX);
        //result+=evaluateBoard(bk,BKING_INDEX);

        //evaluation of pawn boards
        result+=evaluatePawnBoard(wp,WPAWN_INDEX,bp);
        result+=evaluatePawnBoard(bp,BPAWN_INDEX,wp);

        //evaluation of rook boards
        result+=evaluateRookBoard(wr,WROOK_INDEX,wp,bp);
        result+=evaluateRookBoard(br,BROOK_INDEX,bp,wp);

        //evaluation of king boards
        result+=evaluationKingBoard(wk,WKING_INDEX,wp,bp);
        result+=evaluationKingBoard(bk,BKING_INDEX,bp,wp);
        //evaluation of bishop boards
        result+=evaluateBishopBoard(wb,WBISHOP_INDEX,occupied,~white);
        result+=evaluateBishopBoard(bb,BBISHOP_INDEX,occupied,~black);

        return color==1?result:-result;
    }
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
            history[historyPly]=hash;
            historyPly++;
            int score=-quiescence(-beta,-alpha,wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc,ckwc,cqwc,ckbc,cqbc,1-color,move);
            ply--;
            historyPly--;

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
    public static boolean lateMoveReductionCondition(int depth,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,int color,int move,int fullMovesSearched,boolean whiteInCheck,boolean blackInCheck){
        boolean inCheck;
        boolean giveCheck;
        if(color==0){
            inCheck=blackInCheck;
            giveCheck=whiteInCheck;
        }
        else{
            inCheck=whiteInCheck;
            giveCheck=blackInCheck;
        }


        return fullMovesSearched>=MOVE_TO_BE_SEARCHED && depth>=3   && MoveUtilities.extractFromCodedMove(move,3)==0;
    }
    public static boolean nullMovePruningCondition(int depth,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,int color){
        long inCheck;
        if(color==0){
            inCheck=BitBoardMovesGenerator.attackedByWhite(  wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp)&bk;
        }
        else{
            inCheck=BitBoardMovesGenerator.attackedByBlack(  wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp)&wk;
        }
        return inCheck==0 && depth>=3 && ply>0;
    }
    public static boolean detectRepetitions(){
        for(int i=historyPly-2;i>=0;i--){
            if(history[i]==hash)
                return true;
        }
        return false;
    }
    public static final int maxExtensions=6;
    public static int extensions=0;
    public static int negmax(int alpha,int beta,int depth,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,boolean ckw,boolean cqw,boolean ckb,boolean cqb,int color,int lastMove){

        pvLength[ply]=ply;
        //Check if current move is repeated(helps avoid 3-fold repetition)
        if(detectRepetitions() && ply!=0)
            return 0;

        if(depth==0){
            //Search the captures, so that we don't blunder pieces
            return quiescence(alpha,beta,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw,cqw,ckb,cqb,color,lastMove);
            //return evaluate(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,color);
        }
        nodes++;
        if(ply>0 && tt.containsKey(hash)){
            int res= tt.retrieveFromTable(hash,depth,alpha,beta);
            if(res!=invalidValue)
                return res;
        }
        long oldHash=hash;
        if(nullMovePruningCondition(depth,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,color)){
            //null move pruning
            ply++;
            history[historyPly]=hash;
            historyPly++;
            //Hash side repeat
            hash^=ZobristHash.sideHash;
            //Remove en passant from hash if any
            hash=ZobristHash.hashEnPassantRights(hash,0,lastMove,wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp,color);
            int score=-negmax(-beta,-beta +1,depth-1 -2,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw,cqw,ckb,cqb,1-color,0);
            ply--;
            historyPly--;
            if(score>=beta){
                //tt.put(oldHash^depthHash,beta);
                //tt.addToTable(hash,beta,depth,upperBoundType);
                return beta;
            }
        }
        hash= oldHash;
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
        boolean isMate=true;
        int fullMovesSearched=0;
        scorePv=false;
        //Sort The moves, so that we check the strong moves first and prune the week moves later
        moves.sort((a,b)-> Integer.compare(
                scoreMove(a,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp),
                scoreMove(b,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp))*-1);

        followPv=scorePv;
        scorePv=false;
        //variable that indicate what type of node the current one is(used in transposition table)
        int nodeType=lowerBoundType;
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
            boolean whiteCheck=(BitBoardMovesGenerator.attackedByBlack(  wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc)&wkc)!=0;
            boolean blackCheck=(BitBoardMovesGenerator.attackedByWhite(  wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc)&bkc)!=0;
            if((color==1 && whiteCheck)||
                    (color==0&& blackCheck)){
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

            hash=ZobristHash.hashMove(hash,move,lastMove,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw,cqw,ckb,cqb,ckwc,cqwc,ckbc,cqbc,color);
            isMate=false;
            ply++;
            history[historyPly]=hash;
            historyPly++;
            int score;
            //check extensions
            boolean inExtencion=false;
            if( extensions<maxExtensions &&((color==1 && blackCheck)||(color==0 && whiteCheck))){
                depth++;
                extensions++;
                inExtencion=true;
            }
            if(fullMovesSearched==0){
                score=-negmax(-beta,-alpha,depth-1,wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc,ckwc,cqwc,ckbc,cqbc,1-color,move);
            }
            else{
                long endPosition=MoveUtilities.extractFromCodedMove(move,2);
                int endPosType=BitBoardMovesGenerator.getPieceType(endPosition,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp);
                //Late move reduction
                if( endPosType==-1 && lateMoveReductionCondition(depth,wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc,color,move,fullMovesSearched,whiteCheck,blackCheck)){
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
            hash=oldHash;
            ply--;
            historyPly--;
            //re increment extensions
            if( inExtencion) {
                extensions--;
                depth--;
            }

            if(score>alpha){

                //Update history move, if it is not capture
                long fullBoard=wk|wq|wn|wb|wr|wp|bk|bq|bn|bb|br|bp;
                long endPosition=MoveUtilities.extractFromCodedMove(move,2);
                if(((1L<<endPosition)&fullBoard)==0){
                    int startPosType=BitBoardMovesGenerator.getPieceType(MoveUtilities.extractFromCodedMove(move,1),wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp);
                    historyMoves[startPosType][(int)endPosition]+=depth;
                }
                nodeType=exactBoundType;
                alpha=score;
                //Add pv moves to table
                pvTable[ply][ply]=move;
                for(int nextPly=ply+1;nextPly<pvLength[ply+1];nextPly++)
                    pvTable[ply][nextPly]=pvTable[ply+1][nextPly];
                pvLength[ply]=pvLength[ply+1];
                //Prune
                if(score>=beta){
                    //Update killer move only if it is not capture
                    if(((1L<<endPosition)&fullBoard)==0){
                        killerMoves[1][ply]=killerMoves[0][ply];
                        killerMoves[0][ply]=move;
                    }
                    //tt.put(oldHash,beta);
                    tt.addToTable(hash,beta,depth,upperBoundType);
                    return beta;
                }
            }
            fullMovesSearched++;

        }
        if(isMate){
            if((color==1 && (BitBoardMovesGenerator.attackedByBlack(  wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp)&wk)!=0)||
                    (color==0&& (BitBoardMovesGenerator.attackedByWhite(  wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp)&bk)!=0)){
                return -mateVal +ply;
            }
            return 0;
        }
        //tt.put(oldHash,alpha);
        tt.addToTable(hash,alpha,depth,nodeType);

        return alpha;
    }
    public static int getBestMoveIterativeDeepening(int depth,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,boolean ckw,boolean cqw,boolean ckb,boolean cqb,int color,int lastMove){
        //ZobristHash.initializeHashes();
        nodes=0;
        ply=0;
        int alphaIncrement=50;
        int bettaIncrement=50;
        int alpha=-infinity;
        int beta=infinity;
        int score=0;
        followPv=false;
        scorePv=false;

        for(int current_depth=1;current_depth<=depth;current_depth++){
            //nodes=0;
            followPv=true;
            extensions=0;
            score=negmax(alpha,beta,current_depth,wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp,ckw,cqw,ckb,cqb,color,lastMove);
            if ((score <= alpha) || (score >= beta)) {
                alpha = -infinity;
                beta = infinity;
                current_depth--;
                continue;
            }
            alpha = score - alphaIncrement;
            beta = score + bettaIncrement;

            System.out.println("depth "+current_depth+","+"Nodes: "+nodes+","+"score: "+score);
            System.out.print("PVs: ");
            for(int i=0;i<pvLength[0];i++){

                System.out.print(BitBoard.toAlgebra(pvTable[0][i])+", ");
            }
            System.out.println();
        }

        System.out.println("Best score: "+score);
        return pvTable[0][0];
    }
    public static int getBestMove(int depth,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,boolean ckw,boolean cqw,boolean ckb,boolean cqb,int color,int lastMove){
        //ZobristHash.initializeHashes();
        nodes=0;
        ply=0;
        scorePv=false;
        followPv=false;
        int score=negmax(-50000,50000,depth,wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp,ckw,cqw,ckb,cqb,color,lastMove);
        System.out.println("Best score: "+score);
        System.out.print("PVs: ");
        for(int i=0;i<pvLength[0];i++){

            System.out.print(BitBoard.toAlgebra(pvTable[0][i])+", ");
        }
        System.out.println();
        return pvTable[0][0];
    }

}
