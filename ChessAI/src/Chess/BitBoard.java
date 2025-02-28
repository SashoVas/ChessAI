package Chess;

import java.util.ArrayList;
import java.util.List;

public class BitBoard {
    public static final String defaultFen="rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

    public long wk,wq,wn,wb,wr,wp=0L;
    public long bk,bq,bn,bb,br,bp=0L;

    static final long[] FILE_MASKS = new long[8];
    static final long[] RANK_MASKS = new long[8];
    public final long[] DIAGONALS_MASKS= {
            0x1L, 0x102L, 0x10204L, 0x1020408L, 0x102040810L, 0x10204081020L,
            0x1020408102040L, 0x102040810204080L, 0x204081020408000L, 0x408102040800000L,
            0x810204080000000L, 0x1020408000000000L, 0x2040800000000000L, 0x4080000000000000L,
            0x800000000000000L
    };
    public final long[] ANTI_DIAGONALS_MASKS= {
            0x80L, 0x8040L, 0x804020L, 0x80402010L, 0x8040201008L, 0x804020100804L,
            0x80402010080402L, 0x8040201008040201L, 0x4020100804020100L, 0x2010080402010000L,
            0x1008040201000000L, 0x804020100000000L, 0x402010000000000L, 0x201000000000000L,
            0x100000000000000L
    };
    public static long empty;
    public static long occupied;
    public static long blackToTake;
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
    private List<String>generateMovesFromBitBoard(long bitBoard,long startRowIncrement,long startColIncrement,boolean isPromotion){
        List<String> result=new ArrayList<>();
        long first=bitBoard & ~(bitBoard-1);
        while(first!=0){
            long index=Long.numberOfTrailingZeros(first);
            if(!isPromotion){
                result.add(""+index/8+startRowIncrement+index%8+startColIncrement+index/8+index%8);
            }
            else{
                result.add(index/8+startRowIncrement+index%8+startColIncrement+index/8+index%8+"Q");
                result.add(index/8+startRowIncrement+index%8+startColIncrement+index/8+index%8+"R");
                result.add(index/8+startRowIncrement+index%8+startColIncrement+index/8+index%8+"B");
                result.add(index/8+startRowIncrement+index%8+startColIncrement+index/8+index%8+"N");
            }
            bitBoard&=~first;
            first=bitBoard & ~(bitBoard-1);
        }
        return result;
    }
    public List<String> generateMovesW(List<String>history){
        //Optimize using int instead of string
        empty=~(wk|wq|wn|wb|wr|wp|bk|bq|bn|bb|br|bp);
        blackToTake=bq|bn|bb|br|bp;
        List<String> moves=new ArrayList<>();
        //moves.addAll(generatePawnMovesW());
        moves.addAll(generateEnPassantMoves(wp,blackToTake,history));
        return moves;
    }
    public List<String>generateEnPassantMoves(long pawns,long toTake,List<String> movesHistory){
        List<String> result=new ArrayList<>();
        if(movesHistory.size()<3){
            return result;
        }
        String lastMove=movesHistory.get(movesHistory.size()-1);
        if(lastMove.charAt(1)!=lastMove.charAt(3) ||Math.abs(lastMove.charAt(0)-lastMove.charAt(2))!=2 ){
            return result;
        }
        int file=lastMove.charAt(1)-'0';

        //en passant right
        long moves=pawns<<1 & toTake & RANK_MASKS[4] & ~FILE_MASKS[0] & FILE_MASKS[file];
        if(moves!=0){
            long index=Long.numberOfTrailingZeros(moves);
            result.add(""+index/8+""+((index%8)-1)+""+((index/8)+1)+""+index%8);
        }

        //en passant left
        moves=pawns>>1 & toTake & RANK_MASKS[4] & ~FILE_MASKS[7] & FILE_MASKS[file];
        if(moves!=0){
            long index=Long.numberOfTrailingZeros(moves);
            result.add(""+index/8+""+((index%8)+1)+""+((index/8)+1)+""+index%8);
        }
        return result;
    }
    public List<String> generatePawnMovesW(){
        //TODO: Implement en-passant
        List<String> result=new ArrayList<>();
        //capture right
        long moves=wp>>7 & blackToTake & ~RANK_MASKS[7] & ~FILE_MASKS[0];
        result.addAll(generateMovesFromBitBoard(moves,1,-1,false));
        //capture left
        moves= wp>>9 & blackToTake & ~RANK_MASKS[7] & ~FILE_MASKS[7];
        result.addAll(generateMovesFromBitBoard(moves,1,1,false));

        //move one up
        moves=wp>>8 & empty & ~RANK_MASKS[7];
        result.addAll(generateMovesFromBitBoard(moves,1,0,false));

        //move two up
        moves=(wp>>16) & empty &(empty>>8) & RANK_MASKS[3];
        result.addAll(generateMovesFromBitBoard(moves,2,0,false));

        //promotion attack right
        moves=wp>>7 & blackToTake & RANK_MASKS[7] & ~FILE_MASKS[0];
        result.addAll(generateMovesFromBitBoard(moves,1,-1,true));

        //promotion attack left
        moves=wp>>9 & blackToTake & RANK_MASKS[7] & ~FILE_MASKS[7];
        result.addAll(generateMovesFromBitBoard(moves,1,1,true));

        //promotion move one up
        moves=wp>>8 & empty & RANK_MASKS[7];
        result.addAll(generateMovesFromBitBoard(moves,1,0,true));

        //en passant moves
        return result;
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
