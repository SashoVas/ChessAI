package Chess;

import Chess.StandardImplementation.Pieces.Move;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static Chess.AIBot.*;

public class BitBoardMovesGenerator {
    public static long hash=0;
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

    public static void printMasksRanks(){
        for (int i=0;i<8;i++){
            System.out.println(Long.toBinaryString( RANK_MASKS[i]));
            System.out.println("================================");
        }
    }
    public static void printMasksFiles(){
        for (int i=0;i<8;i++){
            System.out.println(Long.toBinaryString( FILE_MASKS[i]));
            System.out.println("================================");
        }
    }
    public static long perftWithUndo(long[] boards,boolean ckw,boolean cqw,boolean ckb,boolean cqb,int depth,int color,int lastMove){
        if (depth==0){
            return 1;
        }
        List<Integer> moves;
        if(color==1){
            moves=generateMovesW(boards[0],boards[1],boards[2],boards[3],boards[4],boards[5],boards[6],boards[7],boards[8],boards[9],boards[10],boards[11],ckw,cqw,lastMove);
        }
        else{
            moves=generateMovesB(boards[0],boards[1],boards[2],boards[3],boards[4],boards[5],boards[6],boards[7],boards[8],boards[9],boards[10],boards[11],ckb,cqb,lastMove);
        }
        long movesCount=0;

        int[]mappings={11,1,2,4,3,9,12,5,6,8,7,10};
        for(int move:moves){
            int endIndex=MoveUtilities.extractFromCodedMove(move,2);
            int captureType=getPieceType(endIndex,boards[0],boards[1],boards[2],boards[3],boards[4],boards[5],boards[6],boards[7],boards[8],boards[9],boards[10],boards[11]);

            //Update castle rules
            boolean ckwc=ckw;
            boolean cqwc=cqw;
            boolean ckbc=ckb;
            boolean cqbc=cqb;
            if(MoveUtilities.extractFromCodedMove(move,3)==0 && MoveUtilities.extractFromCodedMove(move,4)==0){
                //Castle
                long start=MoveUtilities.extractFromCodedMove(move,1);
                if(((1L<<start)&boards[0])!=0){ckwc=false;cqwc=false;}
                if(((1L<<start)&boards[6])!=0){ckbc=false;cqbc=false;}
                if(((1L<<start)&boards[4] &(1L<<63))!=0){ckwc=false;}
                if(((1L<<start)&boards[4] &(1L<<56))!=0){cqwc=false;}
                if(((1L<<start)&boards[10] &(1L<<7))!=0){ckbc=false;}
                if(((1L<<start)&boards[10] &(1L<<0))!=0){cqbc=false;}
            }
            //long[] boardCopy=boards.clone();
            //Make the move
            for(int i=0;i<12;i++){
                boards[i]=makeAMoveOnBoard(boards[i],move,mappings[i]);
            }

            //Check if move is legal
            if((color==1 && ((attackedByBlack(  boards[0],boards[1],boards[2],boards[3],boards[4],boards[5],boards[6],boards[7],boards[8],boards[9],boards[10],boards[11])&boards[0])!=0))||
                    (color==0&& ((attackedByWhite(  boards[0],boards[1],boards[2],boards[3],boards[4],boards[5],boards[6],boards[7],boards[8],boards[9],boards[10],boards[11])&boards[6])!=0))){
                for(int i=0;i<12;i++){
                    boards[i]=undoMoveOnBoard(boards[i],move,mappings[i],captureType!=-1?mappings[captureType]:-1);
                }
                //for(int i=0;i<12;i++){
                //    if(boards[i]!=boardCopy[i]){
                //        String moveAlg=BitBoard.toAlgebra(move);
                //        String lastMoveAlg=BitBoard.toAlgebra(lastMove);
                //        long a=makeAMoveOnBoard(boardCopy[i],move,mappings[i]);
                //        long b=undoMoveOnBoard(a,move,mappings[i],captureType!=-1?mappings[captureType]:-1);
                //        int c=3;
                //    }
                //}
                continue;
            }

            movesCount+=perftWithUndo(boards,ckwc,cqwc,ckbc,cqbc,depth-1,1-color,move);
            for(int i=0;i<12;i++){
                boards[i]=undoMoveOnBoard(boards[i],move,mappings[i],captureType!=-1?mappings[captureType]:-1);
            }
            //for(int i=0;i<12;i++){
            //    if(boards[i]!=boardCopy[i]){
            //        String moveAlg=BitBoard.toAlgebra(move);
            //        String lastMoveAlg=BitBoard.toAlgebra(lastMove);
            //        long expected=boardCopy[i];
            //        long a=makeAMoveOnBoard(expected,move,mappings[i]);
            //        long b=undoMoveOnBoard(a,move,mappings[i],captureType!=-1?mappings[captureType]:-1);
            //        int c=3;
            //    }
            //}
        }
        return movesCount;
    }
    public static long perft(long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,boolean ckw,boolean cqw,boolean ckb,boolean cqb,int depth,int color,int lastMove){
        if (depth==0){
            return 1;
        }
        List<Integer> moves;
        if(color==1){
            moves=generateMovesW(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw,cqw,lastMove);
        }
        else{
            moves=generateMovesB(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckb,cqb,lastMove);
        }
        long movesCount=0;
        long oldHash=hash;
        for(int move:moves){
            //Make the move
            long wkc=makeAMoveOnBoard(wk,move,11);
            long wqc=makeAMoveOnBoard(wq,move,1);
            long wnc=makeAMoveOnBoard(wn,move,2);
            long wbc=makeAMoveOnBoard(wb,move,4);
            long wrc=makeAMoveOnBoard(wr,move,3);
            long wpc=makeAMoveOnBoard(wp,move,9);
            long bkc=makeAMoveOnBoard(bk,move,12);
            long bqc=makeAMoveOnBoard(bq,move,5);
            long bnc=makeAMoveOnBoard(bn,move,6);
            long bbc=makeAMoveOnBoard(bb,move,8);
            long brc=makeAMoveOnBoard(br,move,7);
            long bpc=makeAMoveOnBoard(bp,move,10);

            //Check if move is legal
            if((color==1 && ((attackedByBlack(  wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc)&wkc)!=0))||
                    (color==0&& ((attackedByWhite(  wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc)&bkc)!=0))){
                continue;
            }

            //Update castle rules
            boolean ckwc=ckw;
            boolean cqwc=cqw;
            boolean ckbc=ckb;
            boolean cqbc=cqb;
            if(MoveUtilities.extractFromCodedMove(move,3)==0 && MoveUtilities.extractFromCodedMove(move,4)==0){
            //if(move<10000 || move>=1000000){
                //Castle
                long start=MoveUtilities.extractFromCodedMove(move,1);
                if(((1L<<start)&wk)!=0){ckwc=false;cqwc=false;}
                if(((1L<<start)&bk)!=0){ckbc=false;cqbc=false;}
                if(((1L<<start)&wr &(1L<<63))!=0){ckwc=false;}
                if(((1L<<start)&wr &(1L<<56))!=0){cqwc=false;}
                if(((1L<<start)&br &(1L<<7))!=0){ckbc=false;}
                if(((1L<<start)&br &(1L<<0))!=0){cqbc=false;}
            }

            //hash move
            hash=ZobristHash.hashMove(hash,move,lastMove,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw,cqw,ckb,cqb,ckwc,cqwc,ckbc,cqbc,color);
            //process next moves
            long expected=ZobristHash.hashBoard(wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc,ckwc,cqwc,ckbc,cqbc,1-color,move);
            if(expected!=hash){
                throw new InternalError();
            }
            movesCount+=perft( wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc,ckwc,cqwc,ckbc,cqbc,depth-1,1-color,move);

            //undo hash
            hash=oldHash;

        }
        return movesCount;
    }
    public static String promotionToAlgebra(int promotion){
        switch (promotion){
            case(1):
                return "Q";
            case(2):
                return "N";
            case(3):
                return "R";
            case(4):
                return "B";
            case(5):
                return "q";
            case(6):
                return "n";
            case(7):
                return "r";
            case(8):
                return "b";
            default:
                return "";
        }
    }
    public static int getPieceType(long pieceIndex,long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp){
        long pieceMask=1L<<pieceIndex;

        if((wk&pieceMask)!=0){
            return WKING_INDEX;
        }
        else if((wq&pieceMask)!=0){
            return WQUEEN_INDEX;

        }
        else if((wn&pieceMask)!=0){
            return WKNIGHT_INDEX;

        }
        else if((wb&pieceMask)!=0){
            return WBISHOP_INDEX;

        }
        else if((wr&pieceMask)!=0){
            return WROOK_INDEX;

        }
        else if((wp&pieceMask)!=0){
            return WPAWN_INDEX;
        }
        else if((bk&pieceMask)!=0){
            return BKING_INDEX;
        }
        else if((bq&pieceMask)!=0){
            return BQUEEN_INDEX;

        }
        else if((bn&pieceMask)!=0){
            return BKNIGHT_INDEX;

        }
        else if((bb&pieceMask)!=0){
            return BBISHOP_INDEX;

        }
        else if((br&pieceMask)!=0){
            return BROOK_INDEX;

        }
        else if((bp&pieceMask)!=0){
            return BPAWN_INDEX;
        }
        return -1;
    }
    private static List<Integer>generateMovesFromBitBoard(long bitBoard,long startRowIncrement,long startColIncrement,boolean isPromotion,boolean isWhite,List<Integer> result){
        //promotion mappings
        //1-wq
        //2-wn
        //3-wr
        //4-wb
        //5-bq
        //6-bn
        //7-br
        //8-bb
        long first=bitBoard & -bitBoard;
        while(first!=0){
            long index=Long.numberOfTrailingZeros(first);
            if(!isPromotion){
                result.add(MoveUtilities.codeMove((index/8+startRowIncrement),(index%8+startColIncrement),index/8,index%8,0,0,0));
                //result.add(""+(index/8+startRowIncrement)+(index%8+startColIncrement)+index/8+index%8);
            }
            else if(isWhite){
                result.add(MoveUtilities.codeMove((index/8+startRowIncrement),(index%8+startColIncrement),index/8,index%8,1,0,0));
                result.add(MoveUtilities.codeMove((index/8+startRowIncrement),(index%8+startColIncrement),index/8,index%8,2,0,0));
                result.add(MoveUtilities.codeMove((index/8+startRowIncrement),(index%8+startColIncrement),index/8,index%8,3,0,0));
                result.add(MoveUtilities.codeMove((index/8+startRowIncrement),(index%8+startColIncrement),index/8,index%8,4,0,0));

                //result.add((index/8+startRowIncrement)+(index%8+startColIncrement)+index/8+index%8+"Q");
                //result.add((index/8+startRowIncrement)+(index%8+startColIncrement)+index/8+index%8+"R");
                //result.add((index/8+startRowIncrement)+(index%8+startColIncrement)+index/8+index%8+"B");
                //result.add((index/8+startRowIncrement)+(index%8+startColIncrement)+index/8+index%8+"N");
            }
            else if(!isWhite){
                result.add(MoveUtilities.codeMove((index/8+startRowIncrement),(index%8+startColIncrement),index/8,index%8,5,0,0));
                result.add(MoveUtilities.codeMove((index/8+startRowIncrement),(index%8+startColIncrement),index/8,index%8,6,0,0));
                result.add(MoveUtilities.codeMove((index/8+startRowIncrement),(index%8+startColIncrement),index/8,index%8,7,0,0));
                result.add(MoveUtilities.codeMove((index/8+startRowIncrement),(index%8+startColIncrement),index/8,index%8,8,0,0));

                //result.add((index/8+startRowIncrement)+(index%8+startColIncrement)+index/8+index%8+"q");
                //result.add((index/8+startRowIncrement)+(index%8+startColIncrement)+index/8+index%8+"r");
                //result.add((index/8+startRowIncrement)+(index%8+startColIncrement)+index/8+index%8+"b");
                //result.add((index/8+startRowIncrement)+(index%8+startColIncrement)+index/8+index%8+"n");
            }
            bitBoard&=~first;
            first=bitBoard & -bitBoard;
        }
        return result;
    }
    public static List<Integer> generateMovesW(long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,boolean ck,boolean cq,int lastMove){
        //Optimize using int instead of string
        long empty=~(wk|wq|wn|wb|wr|wp|bk|bq|bn|bb|br|bp);
        long blackToTake=bq|bn|bb|br|bp;
        long occupied=~empty;
        long notWhiteToMove=~(wk|wq|wn|wb|wr|wp|bk);
        long blackOrEmpty=blackToTake|empty;
        List<Integer> moves=new ArrayList<>();
        generatePawnMovesW(wp,empty,blackToTake,moves);
        generateEnPassantMovesW(wp,bp,lastMove,moves);
        generateBishopMoves(wb,blackOrEmpty,occupied,moves);
        generateQueenMoves(wq,blackOrEmpty,occupied,moves);
        generateRookMoves(wr,blackOrEmpty,occupied,moves);
        generateKnightsMoves(wn,notWhiteToMove,moves);
        generateKingMoves(wk,notWhiteToMove,moves);
        long unsafe=attackedByBlack(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp);
        generateCastleWhite(wk,wr,occupied,ck,cq,unsafe,moves);
        return moves;
    }
    public static List<Integer> generateMovesB(long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp,boolean ck,boolean cq,int lastMove){
        //Optimize using int instead of string
        long empty=~(wk|wq|wn|wb|wr|wp|bk|bq|bn|bb|br|bp);
        long whiteToTake=wq|wn|wb|wr|wp;
        long occupied=~empty;
        long notBlackToMove=~(bk|bq|bn|bb|br|bp|wk);
        long whiteOrEmpty=whiteToTake|empty;
        List<Integer> moves=new ArrayList<>();
        generatePawnMovesB(bp,empty,whiteToTake,moves);
        generateEnPassantMovesB(bp,wp,lastMove,moves);
        generateBishopMoves(bb,whiteOrEmpty,occupied,moves);
        generateQueenMoves(bq,whiteOrEmpty,occupied,moves);
        generateRookMoves(br,whiteOrEmpty,occupied,moves);
        generateKnightsMoves(bn,notBlackToMove,moves);
        generateKingMoves(bk,notBlackToMove,moves);
        long unsafe=attackedByWhite(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp);
        generateCastleBlack(bk,br,occupied,ck,cq,unsafe,moves);
        return moves;
    }
    public static long attackedByBlack(long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp){
        long occupied=wk|wq|wn|wb|wr|wp|bk|bq|bn|bb|br|bp;

        long result=0L;
        //pawns
        result|=bp<<7   & ~FILE_MASKS[7];
        result|=bp<<9 & ~FILE_MASKS[0];

        //knights
        long i=bn& -bn;
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
            i=bn& -bn;
        }

        //bishop and queen
        long bishopOrQueen=bb|bq;
        i=bishopOrQueen& -bishopOrQueen;
        while(i!=0){
            int bishopIndex=Long.numberOfTrailingZeros(i);
            moves=generateDiagonalMoves(bishopIndex,occupied) ;
            result|=moves;
            bishopOrQueen&=~i;
            i=bishopOrQueen& -bishopOrQueen;
        }
        //rook and queen
        long rookOrQueen=br|bq;
        i=rookOrQueen& -rookOrQueen;
        while(i!=0){
            int bishopIndex=Long.numberOfTrailingZeros(i);
            moves=generateHorAndVertPMoves(bishopIndex,occupied);
            result|=moves;
            rookOrQueen&=~i;
            i=rookOrQueen& -rookOrQueen;
        }
        //king
        i=bk& -bk;

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
            i=bk& -bk;
        }
        return result;
    }
    public static long attackedByWhite(long wk,long wq,long wn,long wb,long wr,long wp,long bk,long bq,long bn,long bb,long br,long bp){
        long occupied=wk|wq|wn|wb|wr|wp|bk|bq|bn|bb|br|bp;

        long result=0L;
        //pawns
        result|=wp>>7  & ~FILE_MASKS[0];
        result|=wp>>9 & ~FILE_MASKS[7];

        //knights
        long i=wn& -wn;
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
            i=wn& -wn;
        }

        //bishop and queen
        long bishopOrQueen=wb|wq;
        i=bishopOrQueen& -bishopOrQueen;
        while(i!=0){
            int bishopIndex=Long.numberOfTrailingZeros(i);
            moves=generateDiagonalMoves(bishopIndex,occupied) ;
            result|=moves;
            bishopOrQueen&=~i;
            i=bishopOrQueen& -bishopOrQueen;
        }
        //rook and queen
        long rookOrQueen=wr|wq;
        i=rookOrQueen& -rookOrQueen;
        while(i!=0){
            int bishopIndex=Long.numberOfTrailingZeros(i);
            moves=generateHorAndVertPMoves(bishopIndex,occupied);
            result|=moves;
            rookOrQueen&=~i;
            i=rookOrQueen& -rookOrQueen;
        }
        //king
        i=wk& -wk;

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
            i=wk& -wk;
        }
        return result;
    }
    public static long generateHorAndVertPMoves(int pos,long occupied){
        long binaryPos=1L<<pos;
        long hor=(occupied - 2* binaryPos)^Long.reverse(Long.reverse(occupied)-2*Long.reverse(binaryPos));
        long vert=((occupied&FILE_MASKS[pos%8])-2*binaryPos)^ Long.reverse(Long.reverse(occupied&FILE_MASKS[pos%8])- (2* Long.reverse(binaryPos)));
        return (hor & RANK_MASKS[7-pos/8]) | (vert & FILE_MASKS[pos%8]);
    }
    public static long generateDiagonalMoves(int pos, long occupied){
        long binaryPos=1L<<pos;
        long diagonal=((occupied&DIAGONALS_MASKS[(pos/8)+(pos%8)])-(2 * binaryPos))^Long.reverse(Long.reverse(occupied&DIAGONALS_MASKS[(pos/8)+(pos%8)])-(2*Long.reverse(binaryPos)));
        long antiDiagonal=((occupied&ANTI_DIAGONALS_MASKS[(pos/8) +7- (pos%8)])-(2*binaryPos))^ Long.reverse(Long.reverse(occupied&ANTI_DIAGONALS_MASKS[(pos/8) +7- (pos%8)])- (2* Long.reverse(binaryPos)));
        return (diagonal & DIAGONALS_MASKS[(pos/8)+(pos%8)]) | (antiDiagonal & ANTI_DIAGONALS_MASKS[(pos/8) +7- (pos%8)]);
    }
    public static long undoMoveOnBoard(long board,int move,int pieceBoard,int capturePiece){
        long start=MoveUtilities.extractFromCodedMove(move,1);
        long end=MoveUtilities.extractFromCodedMove(move,2);
        if(MoveUtilities.extractFromCodedMove(move,3)!=0){
            //Promotion
            if(((board>>>end)&1)==1)
                board&=~(1L<<end);
            if(capturePiece==pieceBoard){
                board|=(1L<<end);
            }
            long promotionPiece=MoveUtilities.extractFromCodedMove(move,3);
            if(promotionPiece<5 && pieceBoard==9)
                board|=(1L<<start);
            else if(promotionPiece>=5 && pieceBoard==10)
                board|=(1L<<start);

        }
        else if(MoveUtilities.extractFromCodedMove(move,4)!=0 && (pieceBoard==10||pieceBoard==9)){
            //EnPassant
            if(((board>>>end)&1)==1){
                board&=~(1L<<end);
                board|=(1L<<start);
                return board;
            }
            long toRemove;
            if(start%8-end%8==1){
                toRemove=(start/8)*8+(start%8-1);
            }
            else{
                toRemove=(start/8)*8+(start%8+1);
            }
            //if(((board>>>toRemove)&1)==1){
                board|=(1L<<toRemove);
            //}
        }
        else if(MoveUtilities.extractFromCodedMove(move,5)!=0 &&(pieceBoard==3 ||pieceBoard==7||pieceBoard==12||pieceBoard==11)){
            //Castle
            if(((board>>>end)&1)==1){
                board&=~(1L<<end);
                board|=(1L<<start);
            }

            long rookStart;
            long rookEnd;
            if(start>end){
                rookStart=start-4;
                rookEnd=rookStart+3;
            }
            else{
                rookStart=start+3;
                rookEnd=rookStart-2;
            }
            if(((board>>>rookEnd)&1)!=0){
                board&=~(1L<<rookEnd);
                board|=(1L<<rookStart);
            }
        }
        else {
            //Standard move
            if(((board>>>end)&1)==1){
                board&=~(1L<<end);
                board|=(1L<<start);
            }
            else if(capturePiece==pieceBoard){
                board|=(1L<<end);
            }
        }
        return board;
    }
    public static long makeAMoveOnBoard(long board,int move,int pieceBoard){

        long start=MoveUtilities.extractFromCodedMove(move,1);
        long end=MoveUtilities.extractFromCodedMove(move,2);
        if(MoveUtilities.extractFromCodedMove(move,3)!=0){
            //Promotion
            if(((board>>>start)&1)==1){
                board&=~(1L<<start);
            }
            else{
                board&=~(1L<<end);
            }
            long promotionPiece=MoveUtilities.extractFromCodedMove(move,3);
            if(promotionPiece==pieceBoard){
                board|=(1L<<end);
            }
        }
        else if(MoveUtilities.extractFromCodedMove(move,4)!=0 && (pieceBoard==10||pieceBoard==9)){
            //EnPassant
            if(((board>>>start)&1)==1){
                board&=~(1L<<start);
                board|=(1L<<end);
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
        else if(MoveUtilities.extractFromCodedMove(move,5)!=0 &&(pieceBoard==3 ||pieceBoard==7||pieceBoard==12||pieceBoard==11)){
            //Castle
            if(((board>>>start)&1)==1){
                board&=~(1L<<start);
                board|=(1L<<end);
            }
            long rookStart;
            long rookEnd;
            if(start>end){
                rookStart=start-4;
                rookEnd=rookStart+3;
            }
            else{
                rookStart=start+3;
                rookEnd=rookStart-2;
            }
            if(((board>>>rookStart)&1)!=0){
                board&=~(1L<<rookStart);
                board|=(1L<<rookEnd);
            }
        }
        else {
            //Standard move
            if(((board>>>start)&1)==1){
                board&=~(1L<<start);
                board|=(1L<<end);
            }
            else{
                board&=~(1L<<end);
            }
        }
        return board;
    }

    public static List<Integer> generateCastleBlack(long king,long rooks,long occupied,boolean ck,boolean cq,long unsafe,List<Integer> result){
        if((unsafe & king) !=0){
            return result;
        }
        if(ck && (((1L<<7)&rooks)!=0)){
            if(((occupied|unsafe)&((1L<<5)|(1L<<6)))==0){
                result.add(MoveUtilities.codeMove(0,4,0,6,0,0,1));
            }
        }
        if(cq && (((1L)&rooks)!=0)) {
            if (((occupied | unsafe) & ((1L << 2) | (1L << 3))) == 0 && (occupied & (1L << 1)) == 0) {
                result.add(MoveUtilities.codeMove(0, 4, 0, 2, 0, 0,1));
            }
        }
        return result;
    }

    public static List<Integer> generateCastleWhite(long king,long rooks,long occupied,boolean ck,boolean cq,long unsafe,List<Integer> result){
        if((unsafe & king) !=0){
            return result;
        }
        if(ck && (((1L<<63)&rooks)!=0)){
            if(((occupied|unsafe)&((1L<<61)|(1L<<62)))==0){
                result.add(MoveUtilities.codeMove(7,4,7,6,0,0,1));
            }
        }
        if(cq && (((1L<<56)&rooks)!=0)) {

            if (((occupied | unsafe) & ((1L << 59) | (1L << 58))) == 0 && (occupied & (1L << 57)) == 0) {
                result.add(MoveUtilities.codeMove(7, 4, 7, 2, 0, 0,1));
            }

        }
        return result;
    }

    public static List<Integer> generateKingMoves(long king,long notMyColorToTake,List<Integer> result){
        //TODO: Test performance with function as parameter
        long i=king& -king;
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
            j=moves& -moves;
            while(j!=0){
                int moveIndex=Long.numberOfTrailingZeros(j);
                result.add(MoveUtilities.codeMove(bishopIndex/8,bishopIndex%8,moveIndex/8,moveIndex%8,0,0,0));

                //result.add(""+bishopIndex/8+bishopIndex%8+moveIndex/8+moveIndex%8);
                moves&=~j;
                j=moves& -moves;
            }
            king&=~i;
            i=king& -king;
        }
        return result;
    }
    public static List<Integer> generateKnightsMoves(long knights,long notMyColorToTake,List<Integer> result){
        //TODO: Test performance with function as parameter
        long i=knights& -knights;
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
            j=moves& -moves;
            while(j!=0){
                int moveIndex=Long.numberOfTrailingZeros(j);
                result.add(MoveUtilities.codeMove(bishopIndex/8,bishopIndex%8,moveIndex/8,moveIndex%8,0,0,0));

                //result.add(""+bishopIndex/8+bishopIndex%8+moveIndex/8+moveIndex%8);
                moves&=~j;
                j=moves& -moves;
            }
            knights&=~i;
            i=knights& -knights;
        }
        return result;
    }
    public static List<Integer> generateQueenMoves(long queens,long toTakeAndEmpty,long occupied,List<Integer> result){
        //TODO: Test performance with function as parameter
        long i=queens& -queens;
        long moves;
        long j;
        while(i!=0){
            int bishopIndex=Long.numberOfTrailingZeros(i);
            moves=(generateDiagonalMoves(bishopIndex,occupied) | generateHorAndVertPMoves(bishopIndex,occupied))  & toTakeAndEmpty;
            j=moves& -moves;
            while(j!=0){
                int moveIndex=Long.numberOfTrailingZeros(j);
                result.add(MoveUtilities.codeMove(bishopIndex/8,bishopIndex%8,moveIndex/8,moveIndex%8,0,0,0));

                //result.add(""+bishopIndex/8+bishopIndex%8+moveIndex/8+moveIndex%8);
                moves&=~j;
                j=moves& -moves;
            }
            queens&=~i;
            i=queens& -queens;
        }
        return result;
    }
    public static List<Integer> generateRookMoves(long rooks,long toTakeAndEmpty,long occupied,List<Integer> result){
        //TODO: Test performance with function as parameter
        long i=rooks& -rooks;
        long moves;
        long j;
        while(i!=0){
            int bishopIndex=Long.numberOfTrailingZeros(i);
            moves=generateHorAndVertPMoves(bishopIndex,occupied) & toTakeAndEmpty;
            j=moves& -moves;
            while(j!=0){
                int moveIndex=Long.numberOfTrailingZeros(j);
                result.add(MoveUtilities.codeMove(bishopIndex/8,bishopIndex%8,moveIndex/8,moveIndex%8,0,0,0));
                //result.add(""+bishopIndex/8+bishopIndex%8+moveIndex/8+moveIndex%8);
                moves&=~j;
                j=moves& -moves;
            }
            rooks&=~i;
            i=rooks& -rooks;
        }
        return result;
    }
    public static List<Integer> generateBishopMoves(long bishops,long toTakeAndEmpty,long occupied,List<Integer> result){
        //TODO: Test performance with function as parameter
        long i=bishops& -bishops;
        long moves;
        long j;
        while(i!=0){
            int bishopIndex=Long.numberOfTrailingZeros(i);
            moves=generateDiagonalMoves(bishopIndex,occupied) & toTakeAndEmpty;
            j=moves& -moves;
            while(j!=0){
                int moveIndex=Long.numberOfTrailingZeros(j);
                result.add(MoveUtilities.codeMove(bishopIndex/8,bishopIndex%8,moveIndex/8,moveIndex%8,0,0,0));
                moves&=~j;
                j=moves& -moves;
            }
            bishops&=~i;
            i=bishops& -bishops;
        }
        return result;
    }
    public static int getEnPassantIndex(int lastMove,int color){
        int start=MoveUtilities.extractFromCodedMove(lastMove,1);
        long end=MoveUtilities.extractFromCodedMove(lastMove,2);
        if(color==1 && (start%8!=end%8 ||Math.abs(start/8-end/8)!=2 ||start/8!=6))
            return -1;
        else if(start%8!=end%8 ||Math.abs(start/8-end/8)!=2 ||start/8!=1)
            return -1;

        return start;
    }
    public static List<Integer>generateEnPassantMovesB(long pawns,long toTake,int lastMove,List<Integer> result){
        int start=MoveUtilities.extractFromCodedMove(lastMove,1);
        long end=MoveUtilities.extractFromCodedMove(lastMove,2);
        if(start%8!=end%8 ||Math.abs(start/8-end/8)!=2 ||start/8!=6){
            return result;
        }
        int file=(int) (start%8);

        //en passant right
        long moves=(pawns>>1) & toTake & RANK_MASKS[3] & ~FILE_MASKS[7] & FILE_MASKS[file];
        if(moves!=0){
            long index=Long.numberOfTrailingZeros(moves);
            result.add(MoveUtilities.codeMove(index/8,((index%8)+1),((index/8)+1),index%8,0,1,0));
            //result.add(""+index/8+""+((index%8)+1)+""+((index/8)+1)+""+index%8+"E");
        }

        //en passant left
        moves=(pawns<<1) & toTake & RANK_MASKS[3] & ~FILE_MASKS[0] & FILE_MASKS[file];
        if(moves!=0){
            long index=Long.numberOfTrailingZeros(moves);
            result.add(MoveUtilities.codeMove(index/8,((index%8)-1),((index/8)+1),index%8,0,1,0));
            //result.add(""+index/8+""+((index%8)-1)+""+((index/8)+1)+""+index%8+"E");
        }
        return result;
    }
    public static List<Integer>generateEnPassantMovesW(long pawns,long toTake,int lastMove,List<Integer> result){
        long start=MoveUtilities.extractFromCodedMove(lastMove,1);
        long end=MoveUtilities.extractFromCodedMove(lastMove,2);
        if(start%8!=end%8 ||Math.abs(start/8-end/8)!=2 ||start/8!=1){
            return result;
        }
        int file=(int) (start%8);
        //en passant right
        long moves=(pawns<<1) & toTake & RANK_MASKS[4] & ~FILE_MASKS[0] & FILE_MASKS[file];
        if(moves!=0){

            long index=Long.numberOfTrailingZeros(moves);
            result.add(MoveUtilities.codeMove(index/8,((index%8)-1),((index/8)-1),index%8,0,1,0));
            //result.add(""+index/8+""+((index%8)-1)+""+((index/8)-1)+""+index%8+"E");
        }

        //en passant left
        moves=(pawns>>1) & toTake & RANK_MASKS[4] & ~FILE_MASKS[7] & FILE_MASKS[file];
        if(moves!=0){
            long index=Long.numberOfTrailingZeros(moves);
            result.add(MoveUtilities.codeMove(index/8,((index%8)+1),((index/8)-1),index%8,0,1,0));
            //result.add(""+index/8+""+((index%8)+1)+""+((index/8)-1)+""+index%8+"E");
        }

        return result;
    }
    public static List<Integer> generatePawnMovesW(long pieces,long empty,long toTake,List<Integer> result){
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
    public static List<Integer> generatePawnMovesB(long pieces,long empty,long toTake,List<Integer> result){
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

}
