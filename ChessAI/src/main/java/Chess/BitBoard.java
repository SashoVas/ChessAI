package Chess;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class BitBoard {
    public static final String defaultFen="rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public long wk,wq,wn,wb,wr,wp=0L;
    public long bk,bq,bn,bb,br,bp=0L;
    boolean ckw=true;
    boolean cqw=true;
    boolean ckb=true;
    boolean cqb=true;
    int currentTurn=1;//1-White,0-Black
    long lastMove=0;


    private BitBoard(){
        for (int i = 7; i >= 0; i--) {
            BitBoardMovesGenerator.FILE_MASKS[i] = 0x0101010101010101L << i;
            BitBoardMovesGenerator.RANK_MASKS[7-i] = 0xFFL << (8 * i);
        }
    }
    public List<Integer>getMovesScore(){
        List<Long>moves;
        if(currentTurn==1){
            moves=generateMovesW();
        }
        else{
            moves=generateMovesB();
        }
        List<Integer>result=new ArrayList<>();
        for(long move:moves){
            result.add(AIBot.scoreMove(move,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp));
        }
        return result;
    }

    public static String toAlgebra(long move){
        String result="";
        long start=BitBoardMovesGenerator.extractFromCodedMove(move,1);
        long end=BitBoardMovesGenerator.extractFromCodedMove(move,2);
        long promotion=BitBoardMovesGenerator.extractFromCodedMove(move,3);
        result=""+(char)('a'+start%8)+(8-start/8)+(char)('a'+end%8)+(8-end/8);
        result+=(promotion==0?"":BitBoardMovesGenerator.promotionToAlgebra((int)promotion));
        return result;
    }
    public long algebraToCode(String move){
        boolean isPromotion=move.length()>4 && move.charAt(4)!=' ';
        long moveStart=(8-(move.charAt(1)-'0'))*8+(move.charAt(0)-'a');
        long moveEnd=(8-(move.charAt(3)-'0'))*8+(move.charAt(2)-'a');
        List<Long>moves;
        if(currentTurn==1){
            moves=generateMovesW();
        }
        else{
            moves=generateMovesB();
        }
        for(long moveCode:moves){
            long codeStart=BitBoardMovesGenerator.extractFromCodedMove(moveCode,1);
            long codeEnd=BitBoardMovesGenerator.extractFromCodedMove(moveCode,2);
            if(codeStart==moveStart&&codeEnd==moveEnd){
                long promotionPiece=BitBoardMovesGenerator.extractFromCodedMove(moveCode,3);
                if(isPromotion && promotionPiece!=0 && move.toLowerCase().charAt(4)==BitBoardMovesGenerator.promotionToAlgebra((int)promotionPiece).toLowerCase().charAt(0)){
                    return moveCode;
                }
                else if(!isPromotion && promotionPiece==0){
                    return moveCode;
                }
            }
        }
        //Invalid state
        return -1;
    }
    public void playBestMove(int depth){
        makeAMove(getBestMove(depth));
    }
    public long getBestMove(int depth){
        return AIBot.getBestMove(depth,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw, cqw, ckb, cqb,currentTurn,lastMove);
    }
    public int evaluate(){
        return AIBot.evaluate(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,currentTurn);
    }
    public long perft(int depth){
        return BitBoardMovesGenerator.perft( wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw, cqw, ckb, cqb,depth,currentTurn,lastMove);
    }
    public List<Long> generateMovesW(){
        return BitBoardMovesGenerator.generateMovesW( wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw,cqw,lastMove);
    }
    public List<Long>generateMovesB(){
        return BitBoardMovesGenerator.generateMovesB( wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckb,cqb,lastMove);
    }

    public long makeAMove(long move){
        //TODO: Implement castle here

        long result=0L;
        wk=BitBoardMovesGenerator.makeAMoveOnBoard(wk,move,11);
        wq=BitBoardMovesGenerator.makeAMoveOnBoard(wq,move,1);
        wn=BitBoardMovesGenerator.makeAMoveOnBoard(wn,move,2);
        wb=BitBoardMovesGenerator.makeAMoveOnBoard(wb,move,4);
        wr=BitBoardMovesGenerator.makeAMoveOnBoard(wr,move,3);
        wp=BitBoardMovesGenerator.makeAMoveOnBoard(wp,move,9);
        bk=BitBoardMovesGenerator.makeAMoveOnBoard(bk,move,12);
        bq=BitBoardMovesGenerator.makeAMoveOnBoard(bq,move,5);
        bn=BitBoardMovesGenerator.makeAMoveOnBoard(bn,move,6);
        bb=BitBoardMovesGenerator.makeAMoveOnBoard(bb,move,8);
        br=BitBoardMovesGenerator.makeAMoveOnBoard(br,move,7);
        bp=BitBoardMovesGenerator.makeAMoveOnBoard(bp,move,10);

        if((currentTurn==1 && ((BitBoardMovesGenerator.attackedByBlack(  wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp)&wk)!=0))||
                (currentTurn==0&& ((BitBoardMovesGenerator.attackedByWhite(  wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp)&bk)!=0))){
            return -1;
        }
        if(move<10000 || move>=1000000){
            //Castle
            long start=BitBoardMovesGenerator.extractFromCodedMove(move,1);
            if(((1L<<start)&wk)!=0){ckw=false;cqw=false;}
            if(((1L<<start)&bk)!=0){ckb=false;cqb=false;}
            if(((1L<<start)&wr &(1L<<63))!=0){ckw=false;}
            if(((1L<<start)&wr &(1L<<56))!=0){cqw=false;}
            if(((1L<<start)&br &(1L<<7))!=0){ckb=false;}
            if(((1L<<start)&br &(1L<<0))!=0){cqb=false;}
        }
        currentTurn=1-currentTurn;
        lastMove=move;
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
        String[] fenFragments;
        if(fen.contains(" w ")){
            fenFragments=fen.split(" w ");
            board.currentTurn=1;
        }
        else{
            fenFragments=fen.split(" b ");
            board.currentTurn=0;
        }
        fen=fenFragments[0];
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
        if (!fenFragments[1].contains("K")){
            board.ckw=false;
        }
        if(!fenFragments[1].contains("Q")){
            board.cqw=false;
        }
        if(!fenFragments[1].contains("k")){
            board.ckb=false;
        }
        if(!fenFragments[1].contains("q")){
            board.cqb=false;
        }
        return board;
    }

}
