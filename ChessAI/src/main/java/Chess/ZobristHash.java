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
        int enPassantIndex=BitBoardMovesGenerator.getEnPassantIndex(lastMove,color);
        if(enPassantIndex!=-1){
            hash^=enpassantHash[enPassantIndex];
        }

        return hash;
    }
}
