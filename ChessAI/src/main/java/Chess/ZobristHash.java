package Chess;

import Chess.AIBot;
import Chess.BitBoard;
import Chess.BitBoardMovesGenerator;
import Chess.MoveUtilities;

import java.security.SecureRandom;

public class ZobristHash {
    public static long sideHash;
    public static long pieceHash[][]=new long[12][64];
    public static long castleHash[]=new long[4];
    public static long enpassantHash[]=new long[64];
    public static long depthHash[]=new long[10];

    public static void initializeHashes(){
        SecureRandom random=new SecureRandom();
        sideHash=random.nextLong();
        for(int i=0;i<12;i++){
            for(int j=0;j<64;j++){
                pieceHash[i][j]=random.nextLong();
            }
        }
        for(int i=0;i<4;i++){
            castleHash[i]= random.nextLong();
        }
        for(int i=0;i<64;i++){
            enpassantHash[i]=random.nextLong();
        }
        for(int i=0;i<10;i++){
            depthHash[i]=random.nextLong();
        }
    }
    public static long updateHash(long hash,long piece,int pieceType){
        long i=piece& -piece;

        while(i!=0){
            int index=Long.numberOfTrailingZeros(i);
            hash^=pieceHash[pieceType][index];
            piece&=~i;
            i=piece& -piece;
        }
        return hash;
    }

    public static long hashBoard(long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,boolean ckw,boolean cqw,boolean ckb,boolean cqb,int color,int lastMove){
        long hash=0;
        //Board hashing
        hash=updateHash(hash,wk,AIBot.WKING_INDEX);
        hash=updateHash(hash,wq,AIBot.WQUEEN_INDEX);
        hash=updateHash(hash,wn,AIBot.WKNIGHT_INDEX);
        hash=updateHash(hash,wb,AIBot.WBISHOP_INDEX);
        hash=updateHash(hash,wr,AIBot.WROOK_INDEX);
        hash=updateHash(hash,wp,AIBot.WPAWN_INDEX);
        hash=updateHash(hash,bk,AIBot.BKING_INDEX);
        hash=updateHash(hash,bq,AIBot.BQUEEN_INDEX);
        hash=updateHash(hash,bn,AIBot.BKNIGHT_INDEX);
        hash=updateHash(hash,bb,AIBot.BBISHOP_INDEX);
        hash=updateHash(hash,br,AIBot.BROOK_INDEX);
        hash=updateHash(hash,bp,AIBot.BPAWN_INDEX);

        //Turns hashing
        if(color==0){
            hash^=sideHash;
        }

        //Castle Hashing
        if(ckw)hash^=castleHash[0];
        if(cqw)hash^=castleHash[1];
        if(ckb)hash^=castleHash[2];
        if(cqb)hash^=castleHash[3];

        //En passant Hashing
        int lastPieceIndex=MoveUtilities.extractEnd(lastMove);
        int lastPieceType=BitBoardMovesGenerator.getPieceType(lastPieceIndex,wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp);
        int enPassantIndex=BitBoardMovesGenerator.getEnPassantIndex(lastMove,1-color);
        if((lastPieceType==AIBot.WPAWN_INDEX || lastPieceType==AIBot.BPAWN_INDEX) && enPassantIndex!=-1){
            hash^=enpassantHash[enPassantIndex];
        }

        return hash;
    }
    public static int promotionPieceIndexToPieceIndex(int promotionPieceIndex){
        switch (promotionPieceIndex){
            case MoveUtilities.WQUEEN_MAPPING:
                return AIBot.WQUEEN_INDEX;
            case MoveUtilities.WKNIGHT_MAPPING:
                return AIBot.WKNIGHT_INDEX;
            case MoveUtilities.WROOK_MAPPING:
                return AIBot.WROOK_INDEX;
            case MoveUtilities.WBISHOP_MAPPING:
                return AIBot.WBISHOP_INDEX;
            case MoveUtilities.BQUEEN_MAPPING:
                return AIBot.BQUEEN_INDEX;
            case MoveUtilities.BKNIGHT_MAPPING:
                return AIBot.BKNIGHT_INDEX;
            case MoveUtilities.BROOK_MAPPING:
                return AIBot.BROOK_INDEX;
            case MoveUtilities.BBISHOP_MAPPING:
                return AIBot.BBISHOP_INDEX;
        }
        return -1;
    }
    public static long hashEnPassantRights(long hash,int move,int lastMove,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,int color){
        int startIndex=MoveUtilities.extractStart(move);
        int startType=BitBoardMovesGenerator.getPieceType(startIndex,wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp);
        int endIndex=MoveUtilities.extractEnd(move);
        int endType=BitBoardMovesGenerator.getPieceType(endIndex,wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp);
        if(startType==AIBot.WPAWN_INDEX ||startType==AIBot.BPAWN_INDEX){
            int currentEnpassant=BitBoardMovesGenerator.getEnPassantIndex(move,color);
            if(currentEnpassant!=-1){
                hash^=enpassantHash[currentEnpassant];
            }
        }
        int lastEnd=MoveUtilities.extractEnd(lastMove);
        int lastStartType=BitBoardMovesGenerator.getPieceType(lastEnd,wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp);
        if(lastStartType==AIBot.WPAWN_INDEX ||lastStartType==AIBot.BPAWN_INDEX) {
            int oldEnpassant=BitBoardMovesGenerator.getEnPassantIndex(lastMove,1-color);
            if(oldEnpassant!=-1){
                hash^=enpassantHash[oldEnpassant];
            }
        }
        return hash;
    }
    public static long hashMove(long hash,int move,int lastMove,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,boolean oldCkw,boolean oldCqw,boolean oldCkb,boolean oldCqb,boolean ckw,boolean cqw,boolean ckb,boolean cqb,int color){
        int startIndex=MoveUtilities.extractStart(move);
        int startType=BitBoardMovesGenerator.getPieceType(startIndex,wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp);
        int endIndex=MoveUtilities.extractEnd(move);
        int endType=BitBoardMovesGenerator.getPieceType(endIndex,wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp);

        //Turn swap hash
        hash^=sideHash;

        //Movement hash
        hash^=pieceHash[startType][startIndex];
        int promotionPiece=MoveUtilities.extractPromotion(move);
        if(promotionPiece!=0){
            hash ^= pieceHash[promotionPieceIndexToPieceIndex(promotionPiece)][endIndex];
        }
        else {
            hash ^= pieceHash[startType][endIndex];
        }
        //Captures Hash
        if(endType!=-1){
            hash^=pieceHash[endType][endIndex];
        }

        //Hash Castle rights

        if(ckw!=oldCkw)hash^=castleHash[0];
        if(cqw!=oldCqw)hash^=castleHash[1];
        if(ckb!=oldCkb)hash^=castleHash[2];
        if(cqb!=oldCqb)hash^=castleHash[3];

        //En passant rights
        if(startType==AIBot.WPAWN_INDEX ||startType==AIBot.BPAWN_INDEX){
            int currentEnpassant=BitBoardMovesGenerator.getEnPassantIndex(move,color);
            if(currentEnpassant!=-1){
                hash^=enpassantHash[currentEnpassant];
            }
        }
        int lastEnd=MoveUtilities.extractEnd(lastMove);
        int lastStartType=BitBoardMovesGenerator.getPieceType(lastEnd,wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp);
        if(lastStartType==AIBot.WPAWN_INDEX ||lastStartType==AIBot.BPAWN_INDEX) {
            int oldEnpassant=BitBoardMovesGenerator.getEnPassantIndex(lastMove,1-color);
            if(oldEnpassant!=-1){
                hash^=enpassantHash[oldEnpassant];
            }
        }

        //En passant
        if(MoveUtilities.isEnPassant(move)){
            int captureIndex;
            if(color==1)
                captureIndex=endIndex+8;
            else
                captureIndex=endIndex-8;
            int capturePieceType=BitBoardMovesGenerator.getPieceType(captureIndex,wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp);
            hash^=pieceHash[capturePieceType][captureIndex];
        }
        //Castle
        if(MoveUtilities.isCastle(move)){
            int rookStart;
            int rookEnd;
            if(startIndex>endIndex){
                rookStart=startIndex-4;
                rookEnd=rookStart+3;
            }
            else{
                rookStart=startIndex+3;
                rookEnd=rookStart-2;
            }
            int pieceType;
            if(color==1)
                pieceType=AIBot.WROOK_INDEX;
            else
                pieceType=AIBot.BROOK_INDEX;

            hash^=pieceHash[pieceType][rookStart];
            hash^=pieceHash[pieceType][rookEnd];
        }
        return hash;
    }

}
