package Chess;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BitBoard {
    public static final String defaultFen="rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

    public long wk,wq,wn,wb,wr,wp=0L;
    public long bk,bq,bn,bb,br,bp=0L;

    public static final long[] FILE_MASKS = new long[8];
    public static final long[] RANK_MASKS = new long[8];
    public static final long[] DIAGONALS_MASKS= {
            0x1L, 0x102L, 0x10204L, 0x1020408L, 0x102040810L, 0x10204081020L,
            0x1020408102040L, 0x102040810204080L, 0x204081020408000L, 0x408102040800000L,
            0x810204080000000L, 0x1020408000000000L, 0x2040800000000000L, 0x4080000000000000L,
            0x8000000000000000L
    };
    public static final long[] ANTI_DIAGONALS_MASKS= {
            0x80L, 0x8040L, 0x804020L, 0x80402010L, 0x8040201008L, 0x804020100804L,
            0x80402010080402L, 0x8040201008040201L, 0x4020100804020100L, 0x2010080402010000L,
            0x1008040201000000L, 0x804020100000000L, 0x402010000000000L, 0x201000000000000L,
            0x100000000000000L
    };
    public static final long KNIGHT_MOVES_MASK=43234889994L;
    public static final long KING_MOVES_MASK=460039L;

    private BitBoard(){
        for (int i = 7; i >= 0; i--) {
            FILE_MASKS[i] = 0x0101010101010101L << i;
            RANK_MASKS[7-i] = 0xFFL << (8 * i);
        }
    }
    public void printMasksRanks(){
        for (int i=0;i<8;i++){
            System.out.println(Long.toBinaryString( RANK_MASKS[i]));
            System.out.println("================================");
        }
    }
    public void printMasksFiles(){
        for (int i=0;i<8;i++){
            System.out.println(Long.toBinaryString( FILE_MASKS[i]));
            System.out.println("================================");
        }
    }
    public static long codeMove(long startRow,long startCol,long endRow,long endCol,long promotionPiece,long isEnPassant){
        return startRow+10*startCol+100*endRow+1000*endCol+10000*promotionPiece+100000*isEnPassant;
    }
    public static boolean isEnPassantFromCode(long code){
        return code >=100000;
    }
    public static long extractFromCodedMove(long code,long toReturn){
        long startRow=code%10;
        code/=10;
        long startCol=code%10;
        if (toReturn==1){
            return startRow*8+startCol;
        }
        code/=10;
        long endRow=code%10;
        code/=10;
        long endCol=code%10;
        if (toReturn==2){
            return endRow*8+endCol;
        }
        code/=10;
        long promotion=code%10;
        return promotion;

    }
    public long perft(int depth,int color){
        return perft( wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,depth,color,0);
    }
    public long perft(long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,int depth,int color,long lastMove){
        if (depth==0){
            return 1;
        }
        List<Long>moves;
        if(color==1){
            moves=generateMovesW(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,lastMove);
        }
        else{
            moves=generateMovesB(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,lastMove);
        }
        long movesCount=0;

        for(long move:moves){
            long wkc=makeAMoveOnBoard(wk,move,'K');
            long wqc=makeAMoveOnBoard(wq,move,'Q');
            long wnc=makeAMoveOnBoard(wn,move,'N');
            long wbc=makeAMoveOnBoard(wb,move,'B');
            long wrc=makeAMoveOnBoard(wr,move,'R');
            long wpc=makeAMoveOnBoard(wp,move,'P');
            long bkc=makeAMoveOnBoard(bk,move,'k');
            long bqc=makeAMoveOnBoard(bq,move,'q');
            long bnc=makeAMoveOnBoard(bn,move,'n');
            long bbc=makeAMoveOnBoard(bb,move,'b');
            long brc=makeAMoveOnBoard(br,move,'r');
            long bpc=makeAMoveOnBoard(bp,move,'p');
            if((color==1 && ((attackedByBlack(  wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc)&wkc)!=0))||
            (color==0&& ((attackedByWhite(  wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc)&bkc)!=0))){
                continue;
            }
            movesCount+=perft( wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc,depth-1,1-color,move);
        }
        return movesCount;
    }
    private List<Long>generateMovesFromBitBoard(long bitBoard,long startRowIncrement,long startColIncrement,boolean isPromotion,boolean isWhite,List<Long> result){

        long first=bitBoard & ~(bitBoard-1);
        while(first!=0){
            long index=Long.numberOfTrailingZeros(first);
            if(!isPromotion){
                result.add(codeMove((index/8+startRowIncrement),(index%8+startColIncrement),index/8,index%8,0,0));
                //result.add(""+(index/8+startRowIncrement)+(index%8+startColIncrement)+index/8+index%8);
            }
            else if(isWhite){
                result.add(codeMove((index/8+startRowIncrement),(index%8+startColIncrement),index/8,index%8,1,0));
                result.add(codeMove((index/8+startRowIncrement),(index%8+startColIncrement),index/8,index%8,2,0));
                result.add(codeMove((index/8+startRowIncrement),(index%8+startColIncrement),index/8,index%8,3,0));
                result.add(codeMove((index/8+startRowIncrement),(index%8+startColIncrement),index/8,index%8,4,0));

                //result.add((index/8+startRowIncrement)+(index%8+startColIncrement)+index/8+index%8+"Q");
                //result.add((index/8+startRowIncrement)+(index%8+startColIncrement)+index/8+index%8+"R");
                //result.add((index/8+startRowIncrement)+(index%8+startColIncrement)+index/8+index%8+"B");
                //result.add((index/8+startRowIncrement)+(index%8+startColIncrement)+index/8+index%8+"N");
            }
            else if(!isWhite){
                result.add(codeMove((index/8+startRowIncrement),(index%8+startColIncrement),index/8,index%8,5,0));
                result.add(codeMove((index/8+startRowIncrement),(index%8+startColIncrement),index/8,index%8,6,0));
                result.add(codeMove((index/8+startRowIncrement),(index%8+startColIncrement),index/8,index%8,7,0));
                result.add(codeMove((index/8+startRowIncrement),(index%8+startColIncrement),index/8,index%8,8,0));

                //result.add((index/8+startRowIncrement)+(index%8+startColIncrement)+index/8+index%8+"q");
                //result.add((index/8+startRowIncrement)+(index%8+startColIncrement)+index/8+index%8+"r");
                //result.add((index/8+startRowIncrement)+(index%8+startColIncrement)+index/8+index%8+"b");
                //result.add((index/8+startRowIncrement)+(index%8+startColIncrement)+index/8+index%8+"n");
            }
            bitBoard&=~first;
            first=bitBoard & ~(bitBoard-1);
        }
        return result;
    }
    public List<Long> generateMovesW(long lastMove){
        return generateMovesW( wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,lastMove);
    }
    public List<Long>generateMovesB(long lastMove){
        return generateMovesB( wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,lastMove);
    }
    public List<Long> generateMovesW(long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,long lastMove){
        //Optimize using int instead of string
        long empty=~(wk|wq|wn|wb|wr|wp|bk|bq|bn|bb|br|bp);
        long blackToTake=bq|bn|bb|br|bp;
        long occupied=~empty;
        long notWhiteToMove=~(wk|wq|wn|wb|wr|wp|bk);
        long blackOrEmpty=blackToTake|empty;
        List<Long> moves=new ArrayList<>();
        generatePawnMovesW(wp,empty,blackToTake,moves);
        generateEnPassantMovesW(wp,bp,lastMove,moves);
        generateBishopMoves(wb,blackOrEmpty,occupied,moves);
        generateQueenMoves(wq,blackOrEmpty,occupied,moves);
        generateRookMoves(wr,blackOrEmpty,occupied,moves);
        generateKnightsMoves(wn,notWhiteToMove,moves);
        generateKingMoves(wk,notWhiteToMove,moves);
        //attackedByWhite(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp);
        return moves;
    }
    public List<Long> generateMovesB(long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,long lastMove){
        //Optimize using int instead of string
        long empty=~(wk|wq|wn|wb|wr|wp|bk|bq|bn|bb|br|bp);
        long whiteToTake=wq|wn|wb|wr|wp;
        long occupied=~empty;
        long notBlackToMove=~(bk|bq|bn|bb|br|bp|wk);
        long whiteOrEmpty=whiteToTake|empty;
        List<Long> moves=new ArrayList<>();
        generatePawnMovesB(bp,empty,whiteToTake,moves);
        generateEnPassantMovesB(bp,wp,lastMove,moves);
        generateBishopMoves(bb,whiteOrEmpty,occupied,moves);
        generateQueenMoves(bq,whiteOrEmpty,occupied,moves);
        generateRookMoves(br,whiteOrEmpty,occupied,moves);
        generateKnightsMoves(bn,notBlackToMove,moves);
        generateKingMoves(bk,notBlackToMove,moves);
        //attackedByBlack(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp);

        return moves;
    }
    public long attackedByBlack(long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp){
        long occupied=wk|wq|wn|wb|wr|wp|bk|bq|bn|bb|br|bp;

        long result=0L;
        //pawns
        result|=bp<<7   & ~FILE_MASKS[7];
        result|=bp<<9 & ~FILE_MASKS[0];

        //knights
        long i=bn&~(bn-1);
        long moves;
        while(i!=0){
            int bishopIndex=Long.numberOfTrailingZeros(i);

            if(bishopIndex>18){
                moves=KNIGHT_MOVES_MASK<<(bishopIndex-18);
            }
            else{
                moves=KNIGHT_MOVES_MASK>>(18-bishopIndex);
            }
            if(bishopIndex%8<4){
                moves&=~(FILE_MASKS[6]|FILE_MASKS[7]);
            }
            else{
                moves&=~(FILE_MASKS[0]|FILE_MASKS[1]);
            }
            result|=moves;
            bn&=~i;
            i=bn&~(bn-1);
        }

        //bishop and queen
        long bishopOrQueen=bb|bq;
        i=bishopOrQueen&~(bishopOrQueen-1);
        while(i!=0){
            int bishopIndex=Long.numberOfTrailingZeros(i);
            moves=generateDiagonalMoves(bishopIndex,occupied) ;
            result|=moves;
            bishopOrQueen&=~i;
            i=bishopOrQueen&~(bishopOrQueen-1);
        }
        //rook and queen
        long rookOrQueen=br|bq;
        i=rookOrQueen&~(rookOrQueen-1);
        while(i!=0){
            int bishopIndex=Long.numberOfTrailingZeros(i);
            moves=generateHorAndVertPMoves(bishopIndex,occupied);
            result|=moves;
            rookOrQueen&=~i;
            i=rookOrQueen&~(rookOrQueen-1);
        }
        //king
        i=bk&~(bk-1);

        while(i!=0){
            int bishopIndex=Long.numberOfTrailingZeros(i);

            if(bishopIndex>9){
                moves=KING_MOVES_MASK<<(bishopIndex-9);
            }
            else{
                moves=KING_MOVES_MASK>>(9-bishopIndex);
            }
            if(bishopIndex%8<4){
                moves&=~(FILE_MASKS[6]|FILE_MASKS[7]);
            }
            else{
                moves&=~(FILE_MASKS[0]|FILE_MASKS[1]);
            }
            result|=moves;
            bk&=~i;
            i=bk&~(bk-1);
        }
        return result;
    }
    public long attackedByWhite(long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp){
        long occupied=wk|wq|wn|wb|wr|wp|bk|bq|bn|bb|br|bp;

        long result=0L;
        //pawns
        result|=wp>>7  & ~FILE_MASKS[0];
        result|=wp>>9 & ~FILE_MASKS[7];

        //knights
        long i=wn&~(wn-1);
        long moves;
        while(i!=0){
            int bishopIndex=Long.numberOfTrailingZeros(i);

            if(bishopIndex>18){
                moves=KNIGHT_MOVES_MASK<<(bishopIndex-18);
            }
            else{
                moves=KNIGHT_MOVES_MASK>>(18-bishopIndex);
            }
            if(bishopIndex%8<4){
                moves&=~(FILE_MASKS[6]|FILE_MASKS[7]);
            }
            else{
                moves&=~(FILE_MASKS[0]|FILE_MASKS[1]);
            }
            result|=moves;
            wn&=~i;
            i=wn&~(wn-1);
        }

        //bishop and queen
        long bishopOrQueen=wb|wq;
        i=bishopOrQueen&~(bishopOrQueen-1);
        while(i!=0){
            int bishopIndex=Long.numberOfTrailingZeros(i);
            moves=generateDiagonalMoves(bishopIndex,occupied) ;
            result|=moves;
            bishopOrQueen&=~i;
            i=bishopOrQueen&~(bishopOrQueen-1);
        }
        //rook and queen
        long rookOrQueen=wr|wq;
        i=rookOrQueen&~(rookOrQueen-1);
        while(i!=0){
            int bishopIndex=Long.numberOfTrailingZeros(i);
            moves=generateHorAndVertPMoves(bishopIndex,occupied);
            result|=moves;
            rookOrQueen&=~i;
            i=rookOrQueen&~(rookOrQueen-1);
        }
        //king
        i=wk&~(wk-1);

        while(i!=0){
            int bishopIndex=Long.numberOfTrailingZeros(i);

            if(bishopIndex>9){
                moves=KING_MOVES_MASK<<(bishopIndex-9);
            }
            else{
                moves=KING_MOVES_MASK>>(9-bishopIndex);
            }
            if(bishopIndex%8<4){
                moves&=~(FILE_MASKS[6]|FILE_MASKS[7]);
            }
            else{
                moves&=~(FILE_MASKS[0]|FILE_MASKS[1]);
            }
            result|=moves;
            wk&=~i;
            i=wk&~(wk-1);
        }
        return result;
    }
    public long generateHorAndVertPMoves(int pos,long occupied){
        long binaryPos=1L<<pos;
        long hor=(occupied - 2* binaryPos)^Long.reverse(Long.reverse(occupied)-2*Long.reverse(binaryPos));
        long vert=((occupied&FILE_MASKS[pos%8])-2*binaryPos)^ Long.reverse(Long.reverse(occupied&FILE_MASKS[pos%8])- (2* Long.reverse(binaryPos)));
        return (hor & RANK_MASKS[7-pos/8]) | (vert & FILE_MASKS[pos%8]);
    }
    public long generateDiagonalMoves(int pos, long occupied){
        long binaryPos=1L<<pos;
        long diagonal=((occupied&DIAGONALS_MASKS[(pos/8)+(pos%8)])-(2 * binaryPos))^Long.reverse(Long.reverse(occupied&DIAGONALS_MASKS[(pos/8)+(pos%8)])-(2*Long.reverse(binaryPos)));
        long antiDiagonal=((occupied&ANTI_DIAGONALS_MASKS[(pos/8) +7- (pos%8)])-(2*binaryPos))^ Long.reverse(Long.reverse(occupied&ANTI_DIAGONALS_MASKS[(pos/8) +7- (pos%8)])- (2* Long.reverse(binaryPos)));
        return (diagonal & DIAGONALS_MASKS[(pos/8)+(pos%8)]) | (antiDiagonal & ANTI_DIAGONALS_MASKS[(pos/8) +7- (pos%8)]);
    }
    public long makeAMove(long move){
        long result=0L;
        wk=makeAMoveOnBoard(wk,move,'K');
        wq=makeAMoveOnBoard(wq,move,'Q');
        wn=makeAMoveOnBoard(wn,move,'N');
        wb=makeAMoveOnBoard(wb,move,'B');
        wr=makeAMoveOnBoard(wr,move,'R');
        wp=makeAMoveOnBoard(wp,move,'P');
        bk=makeAMoveOnBoard(bk,move,'k');
        bq=makeAMoveOnBoard(bq,move,'q');
        bn=makeAMoveOnBoard(bn,move,'n');
        bb=makeAMoveOnBoard(bb,move,'b');
        br=makeAMoveOnBoard(br,move,'r');
        bp=makeAMoveOnBoard(bp,move,'p');

        return result;
    }
    public long makeAMoveOnBoard(long board,long move,char pieceBoard){
        //int start=(move.charAt(0)-'0')*8+(move.charAt(1)-'0');
        //int end=(move.charAt(2)-'0')*8+(move.charAt(3)-'0');

        long start=extractFromCodedMove(move,1);
        long end=extractFromCodedMove(move,2);
        if (move<10000){
            if(((board>>>start)&1)==1){
                board&=~(1L<<start);
                board|=(1L<<end);
            }
            else{
                board&=~(1L<<end);
            }
        }
        else if(move<100000){
            if(((board>>>start)&1)==1){
                board&=~(1L<<start);
            }
            else{
                board&=~(1L<<end);
            }

            //if(move.charAt(move.length()-1)==pieceBoard){
            //    board|=(1L<<end);
            //}
        }
        else if(move >=100000){
            if(((board>>>start)&1)==1){
                board&=~(1L<<start);
                board|=(1L<<end);
            }
            else{
                board&=~(1L<<end);
            }
            long toRemove;
            if(start%8-end%8==1){
                toRemove=(start/8)*8+(start%8-1);
            }
            else{
                toRemove=(start/8)*8+(start%8+1);
            }
            if(((board>>>toRemove)&1)==1){
                board&=~(1L<<toRemove);
            }
        }


        return board;
    }

    public List<Long> generateKingMoves(long king,long notMyColorToTake,List<Long> result){
        //TODO: Test performance with function as parameter
        long i=king&~(king-1);
        long moves;
        long j;
        while(i!=0){
            int bishopIndex=Long.numberOfTrailingZeros(i);

            if(bishopIndex>9){
                moves=KING_MOVES_MASK<<(bishopIndex-9);
            }
            else{
                moves=KING_MOVES_MASK>>(9-bishopIndex);
            }
            if(bishopIndex%8<4){
                moves&=~(FILE_MASKS[6]|FILE_MASKS[7])&notMyColorToTake;
            }
            else{
                moves&=~(FILE_MASKS[0]|FILE_MASKS[1])&notMyColorToTake;
            }
            j=moves&~(moves-1);
            while(j!=0){
                int moveIndex=Long.numberOfTrailingZeros(j);
                result.add(codeMove(bishopIndex/8,bishopIndex%8,moveIndex/8,moveIndex%8,0,0));

                //result.add(""+bishopIndex/8+bishopIndex%8+moveIndex/8+moveIndex%8);
                moves&=~j;
                j=moves&~(moves-1);
            }
            king&=~i;
            i=king&~(king-1);
        }
        return result;
    }
    public List<Long> generateKnightsMoves(long knights,long notMyColorToTake,List<Long> result){
        //TODO: Test performance with function as parameter
        long i=knights&~(knights-1);
        long moves;
        long j;
        while(i!=0){
            int bishopIndex=Long.numberOfTrailingZeros(i);

            if(bishopIndex>18){
                moves=KNIGHT_MOVES_MASK<<(bishopIndex-18);
            }
            else{
                moves=KNIGHT_MOVES_MASK>>(18-bishopIndex);
            }
            //printMask(moves);
            if(bishopIndex%8<4){
                moves&=~(FILE_MASKS[6]|FILE_MASKS[7])&notMyColorToTake;
            }
            else{
                moves&=~(FILE_MASKS[0]|FILE_MASKS[1])&notMyColorToTake;
            }
            j=moves&~(moves-1);
            while(j!=0){
                int moveIndex=Long.numberOfTrailingZeros(j);
                result.add(codeMove(bishopIndex/8,bishopIndex%8,moveIndex/8,moveIndex%8,0,0));

                //result.add(""+bishopIndex/8+bishopIndex%8+moveIndex/8+moveIndex%8);
                moves&=~j;
                j=moves&~(moves-1);
            }
            knights&=~i;
            i=knights&~(knights-1);
        }
        return result;
    }
    public List<Long> generateQueenMoves(long queens,long toTakeAndEmpty,long occupied,List<Long> result){
        //TODO: Test performance with function as parameter
        long i=queens&~(queens-1);
        long moves;
        long j;
        while(i!=0){
            int bishopIndex=Long.numberOfTrailingZeros(i);
            moves=(generateDiagonalMoves(bishopIndex,occupied) | generateHorAndVertPMoves(bishopIndex,occupied))  & toTakeAndEmpty;
            j=moves&~(moves-1);
            while(j!=0){
                int moveIndex=Long.numberOfTrailingZeros(j);
                result.add(codeMove(bishopIndex/8,bishopIndex%8,moveIndex/8,moveIndex%8,0,0));

                //result.add(""+bishopIndex/8+bishopIndex%8+moveIndex/8+moveIndex%8);
                moves&=~j;
                j=moves&~(moves-1);
            }
            queens&=~i;
            i=queens&~(queens-1);
        }
        return result;
    }
    public List<Long> generateRookMoves(long rooks,long toTakeAndEmpty,long occupied,List<Long> result){
        //TODO: Test performance with function as parameter
        long i=rooks&~(rooks-1);
        long moves;
        long j;
        while(i!=0){
            int bishopIndex=Long.numberOfTrailingZeros(i);
            moves=generateHorAndVertPMoves(bishopIndex,occupied) & toTakeAndEmpty;
            j=moves&~(moves-1);
            while(j!=0){
                int moveIndex=Long.numberOfTrailingZeros(j);
                result.add(codeMove(bishopIndex/8,bishopIndex%8,moveIndex/8,moveIndex%8,0,0));
                //result.add(""+bishopIndex/8+bishopIndex%8+moveIndex/8+moveIndex%8);
                moves&=~j;
                j=moves&~(moves-1);
            }
            rooks&=~i;
            i=rooks&~(rooks-1);
        }
        return result;
    }
    public List<Long> generateBishopMoves(long bishops,long toTakeAndEmpty,long occupied,List<Long> result){
        //TODO: Test performance with function as parameter
        long i=bishops&~(bishops-1);
        long moves;
        long j;
        while(i!=0){
            int bishopIndex=Long.numberOfTrailingZeros(i);
            moves=generateDiagonalMoves(bishopIndex,occupied) & toTakeAndEmpty;
            j=moves&~(moves-1);
            while(j!=0){
                int moveIndex=Long.numberOfTrailingZeros(j);
                result.add(codeMove(bishopIndex/8,bishopIndex%8,moveIndex/8,moveIndex%8,0,0));
                moves&=~j;
                j=moves&~(moves-1);
            }
            bishops&=~i;
            i=bishops&~(bishops-1);
        }
        return result;
    }
    public List<Long>generateEnPassantMovesB(long pawns,long toTake,long lastMove,List<Long> result){
        long start=extractFromCodedMove(lastMove,1);
        long end=extractFromCodedMove(lastMove,2);
        if(start%8!=end%8 ||Math.abs(start/8-end/8)!=2 ||start/8!=6){
            return result;
        }
        int file=(int) (start%8);

        //en passant right
        long moves=(pawns>>1) & toTake & RANK_MASKS[3] & ~FILE_MASKS[7] & FILE_MASKS[file];
        if(moves!=0){
            long index=Long.numberOfTrailingZeros(moves);
            result.add(codeMove(index/8,((index%8)+1),((index/8)+1),index%8,0,1));
            //result.add(""+index/8+""+((index%8)+1)+""+((index/8)+1)+""+index%8+"E");
        }

        //en passant left
        moves=(pawns<<1) & toTake & RANK_MASKS[3] & ~FILE_MASKS[0] & FILE_MASKS[file];
        if(moves!=0){
            long index=Long.numberOfTrailingZeros(moves);
            result.add(codeMove(index/8,((index%8)-1),((index/8)+1),index%8,0,1));
            //result.add(""+index/8+""+((index%8)-1)+""+((index/8)+1)+""+index%8+"E");
        }
        return result;
    }
    public List<Long>generateEnPassantMovesW(long pawns,long toTake,long lastMove,List<Long> result){
        long start=extractFromCodedMove(lastMove,1);
        long end=extractFromCodedMove(lastMove,2);
        if(start%8!=end%8 ||Math.abs(start/8-end/8)!=2 ||start/8!=1){
            return result;
        }
        int file=(int) (start%8);
        //en passant right
        long moves=(pawns<<1) & toTake & RANK_MASKS[4] & ~FILE_MASKS[0] & FILE_MASKS[file];
        if(moves!=0){

            long index=Long.numberOfTrailingZeros(moves);
            result.add(codeMove(index/8,((index%8)-1),((index/8)-1),index%8,0,1));
            //result.add(""+index/8+""+((index%8)-1)+""+((index/8)-1)+""+index%8+"E");
        }

        //en passant left
        moves=(pawns>>1) & toTake & RANK_MASKS[4] & ~FILE_MASKS[7] & FILE_MASKS[file];
        if(moves!=0){
            long index=Long.numberOfTrailingZeros(moves);
            result.add(codeMove(index/8,((index%8)+1),((index/8)-1),index%8,0,1));
            //result.add(""+index/8+""+((index%8)+1)+""+((index/8)-1)+""+index%8+"E");
        }

        return result;
    }
    public List<Long> generatePawnMovesW(long pieces,long empty,long toTake,List<Long> result){
        //capture right
        long moves=pieces>>7 & toTake & ~RANK_MASKS[7] & ~FILE_MASKS[0];
        generateMovesFromBitBoard(moves,1,-1,false,true,result);
        //capture left
        moves= pieces>>9 & toTake & ~RANK_MASKS[7] & ~FILE_MASKS[7];
        generateMovesFromBitBoard(moves,1,1,false,true,result);

        //move one up
        moves=pieces>>8 & empty & ~RANK_MASKS[7];
        generateMovesFromBitBoard(moves,1,0,false,true,result);

        //move two up
        moves=(pieces>>16) & empty &(empty>>8) & RANK_MASKS[3];
        generateMovesFromBitBoard(moves,2,0,false,true,result);

        //promotion attack right
        moves=pieces>>7 & toTake & RANK_MASKS[7] & ~FILE_MASKS[0];
        generateMovesFromBitBoard(moves,1,-1,true,true,result);

        //promotion attack left
        moves=pieces>>9 & toTake & RANK_MASKS[7] & ~FILE_MASKS[7];
        generateMovesFromBitBoard(moves,1,1,true,true,result);

        //promotion move one up
        moves=pieces>>8 & empty & RANK_MASKS[7];
        generateMovesFromBitBoard(moves,1,0,true,true,result);

        //en passant moves
        return result;
    }
    public List<Long> generatePawnMovesB(long pieces,long empty,long toTake,List<Long> result){
        //capture right
        long moves=pieces<<7 & toTake & ~RANK_MASKS[0] & ~FILE_MASKS[7];
        generateMovesFromBitBoard(moves,-1,1,false,false,result);
        //capture left
        moves= pieces<<9 & toTake & ~RANK_MASKS[0] & ~FILE_MASKS[0];
        generateMovesFromBitBoard(moves,-1,-1,false,false,result);

        //move one up
        moves=pieces<<8 & empty & ~RANK_MASKS[0];
        generateMovesFromBitBoard(moves,-1,0,false,false,result);

        //move two up
        moves=(pieces<<16) & empty &(empty<<8) & RANK_MASKS[4];
        generateMovesFromBitBoard(moves,-2,0,false,false,result);

        //promotion attack right
        moves=pieces<<7 & toTake & RANK_MASKS[0] & ~FILE_MASKS[7];
        generateMovesFromBitBoard(moves,-1,1,true,false,result);

        //promotion attack left
        moves=pieces<<9 & toTake & RANK_MASKS[0] & ~FILE_MASKS[0];
        generateMovesFromBitBoard(moves,-1,-1,true,false,result);

        //promotion move one up
        moves=pieces<<8 & empty & RANK_MASKS[0];
        generateMovesFromBitBoard(moves,-1,0,true,false,result);

        //en passant moves
        return result;
    }
    public static void visualizeMoves(List<String>moves){
        String[][] result=new String[8][8];
        for(String move:moves){
            result[move.charAt(2)-'0'][move.charAt(3)-'0']="#";
        }

        for(int i=0;i<8;i++){
            System.out.println("");
            System.out.print("|");
            for(int j=0;j<8;j++){
                if(result[i][j]==null){
                    System.out.print(" |");
                }
                else{
                    System.out.print(result[i][j]+"|");

                }
            }
        }
        System.out.println("");

    }
    public static void printMask(long mask){
        for(int i=0;i<64;i++){
            if(i%8==0){
                System.out.println("");
                System.out.print("|");
            }
            if(((mask>>i)&1)==1){
                System.out.print("#|");
            }
            else{
                System.out.print(" |");
            }
        }
        System.out.println("");
    }
    public void printBoard(){
        for(int i=0;i<64;i++){
            if(i%8==0){
                System.out.println("");
                System.out.print("|");
            }
            if(((bk>>i)&1)==1){
                System.out.print("k|");
            }
            else if(((bq>>i)&1)==1){
                System.out.print("q|");
            }
            else if(((bn>>i)&1)==1){
                System.out.print("n|");
            }
            else if(((br>>i)&1)==1){
                System.out.print("r|");
            }
            else if(((bb>>i)&1)==1){
                System.out.print("b|");
            }
            else if(((bp>>i)&1)==1){
                System.out.print("p|");
            }

            else if(((wk>>i)&1)==1){
                System.out.print("K|");
            }
            else if(((wq>>i)&1)==1){
                System.out.print("Q|");
            }
            else if(((wn>>i)&1)==1){
                System.out.print("N|");
            }
            else if(((wr>>i)&1)==1){
                System.out.print("R|");
            }
            else if(((wb>>i)&1)==1){
                System.out.print("B|");
            }
            else if(((wp>>i)&1)==1){
                System.out.print("P|");
            }
            else{
                System.out.print(" |");
            }
        }
        System.out.println("");

    }

    public static BitBoard createBoardFromFen(String fen){
        BitBoard board=new BitBoard();
        int row=0;
        int col=0;
        for (int i=0;i<fen.length();i++){
            if(fen.charAt(i)=='/'){
                row++;
                col=0;
            }
            else if(fen.charAt(i)<='9'){
                col+=fen.charAt(i)-'0';
            }
            else{
                switch (fen.charAt(i)) {
                    case 'k':
                        board.bk+=1L<<row*8+col;
                        break;
                    case 'n':
                        board.bn+=1L<<row*8+col;
                        break;
                    case 'r':
                        board.br+=1L<<row*8+col;
                        break;
                    case 'b':
                        board.bb+=1L<<row*8+col;
                        break;
                    case 'q':
                        board.bq+=1L<<row*8+col;
                        break;
                    case 'p':
                        board.bp+=1L<<row*8+col;
                        break;
                    case 'K':
                        board.wk+=1L<<row*8+col;
                        break;
                    case 'N':
                        board.wn+=1L<<row*8+col;
                        break;
                    case 'R':
                        board.wr+=1L<<row*8+col;
                        break;
                    case 'B':
                        board.wb+=1L<<row*8+col;
                        break;
                    case 'Q':
                        board.wq+=1L<<row*8+col;
                        break;
                    case 'P':
                        board.wp+=1L<<row*8+col;
                        break;
                    default:
                        break;
                }
                col++;
            }
        }
        return board;
    }

}
