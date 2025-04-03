package Chess.Evaluation;

import Chess.AIBot;
import Chess.Moves.BitBoardMovesGenerator;
import Chess.Moves.MoveUtilities;

public class MoveEvaluation {
    public long[][] killerMoves=new long[2][64];
    public int[][] historyMoves=new int[12][64];
    public  int[] pvLength=new int[AIBot.MAX_PLY];
    public  int[][] pvTable=new int[AIBot.MAX_PLY][64];
    public boolean followPv=false;
    public boolean scorePv=false;
    private final int[][] MVV_LVA = {
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
    public int scoreMove(int move,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,int bestMove,int ply){
        if(move==bestMove)
            return 30000;

        long targetIndex= MoveUtilities.extractEnd(move);
        int targetType= BitBoardMovesGenerator.getPieceType(targetIndex,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp);

        if(followPv && pvTable[0][ply]==move){
            scorePv=true;
            return 20000;
        }
        if(targetType!=-1){
            //score capture moves higher than others
            long startIndex=MoveUtilities.extractStart(move);
            int startType=BitBoardMovesGenerator.getPieceType(startIndex,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp);

            return MVV_LVA[startType][targetType]+ 10000;
        }
        else if(MoveUtilities.isEnPassant(move) ){
            //score en passant as capture
            return MVV_LVA[AIBot.WPAWN_INDEX][AIBot.WPAWN_INDEX] + 10000;
        }

        //score moves that are not captures, but produce cut ofs
        if(move==killerMoves[0][ply])
            return 9000;
        else if(move==killerMoves[1][ply])
            return 8000;

        //score castle and promotions higher
        if(MoveUtilities.isCastle(move) || MoveUtilities.isPromotion(move))
            return 5000;

        //score moves that were good before
        long startIndex=MoveUtilities.extractStart(move);
        int startType=BitBoardMovesGenerator.getPieceType(startIndex,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp);
        return historyMoves[startType][(int)targetIndex];
    }

}
