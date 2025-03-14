package Chess;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
    int lastMove=0;


    private BitBoard(){
        for (int i = 7; i >= 0; i--) {
            BitBoardMovesGenerator.FILE_MASKS[i] = 0x0101010101010101L << i;
            BitBoardMovesGenerator.RANK_MASKS[7-i] = 0xFFL << (8 * i);
        }
        ZobristHash.initializeHashes();

    }
    public long getBoardHash(){
        return ZobristHash.hashBoard(wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp,ckw,cqw,ckb,cqb,currentTurn,lastMove);
    }
    public List<Integer>getMovesOrdered(){
        List<Integer>moves;
        if(currentTurn==1){
            moves=generateMovesW();
        }
        else{
            moves=generateMovesB();
        }
        moves.sort((a,b)-> Integer.compare(
                AIBot.scoreMove(a,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp),
                AIBot.scoreMove(b,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp))*-1);
        for(int move:moves){
            System.out.println(AIBot.scoreMove(move,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp));
        }
        return moves;

    }
    public List<Integer>getMovesScore(){
        List<Integer>moves;
        if(currentTurn==1){
            moves=generateMovesW();
        }
        else{
            moves=generateMovesB();
        }
        List<Integer>result=new ArrayList<>();
        for(int move:moves){
            result.add(AIBot.scoreMove(move,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp));
        }
        return result;
    }

    public static String toAlgebra(int move){
        String result="";
        long start=MoveUtilities.extractFromCodedMove(move,1);
        long end=MoveUtilities.extractFromCodedMove(move,2);
        long promotion=MoveUtilities.extractFromCodedMove(move,3);
        result=""+(char)('a'+start%8)+(8-start/8)+(char)('a'+end%8)+(8-end/8);
        result+=(promotion==0?"":BitBoardMovesGenerator.promotionToAlgebra((int)promotion));
        return result;
    }
    public int algebraToCode(String move){
        boolean isPromotion=move.length()>4 && move.charAt(4)!=' ';
        long moveStart=(8-(move.charAt(1)-'0'))*8+(move.charAt(0)-'a');
        long moveEnd=(8-(move.charAt(3)-'0'))*8+(move.charAt(2)-'a');
        List<Integer>moves;
        if(currentTurn==1){
            moves=generateMovesW();
        }
        else{
            moves=generateMovesB();
        }
        for(int moveCode:moves){
            long codeStart=MoveUtilities.extractFromCodedMove(moveCode,1);
            long codeEnd=MoveUtilities.extractFromCodedMove(moveCode,2);
            if(codeStart==moveStart&&codeEnd==moveEnd){
                long promotionPiece=MoveUtilities.extractFromCodedMove(moveCode,3);
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
    public int getBestMove(int depth){
        return AIBot.getBestMove(depth,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw, cqw, ckb, cqb,currentTurn,lastMove);
    }
    public int evaluate(){
        return AIBot.evaluate(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,currentTurn);
    }
    public long perft(int depth){
        BitBoardMovesGenerator.hash=getBoardHash();
        return BitBoardMovesGenerator.perft( wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw, cqw, ckb, cqb,depth,currentTurn,lastMove);
    }
    public List<Integer> generateMovesW(){
        return BitBoardMovesGenerator.generateMovesW( wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw,cqw,lastMove);
    }
    public List<Integer>generateMovesB(){
        return BitBoardMovesGenerator.generateMovesB( wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckb,cqb,lastMove);
    }

    public long makeAMove(int move){
        //TODO: Implement castle here

        long result=0L;
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

        if((currentTurn==1 && ((BitBoardMovesGenerator.attackedByBlack(  wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc)&wkc)!=0))||
                (currentTurn==0&& ((BitBoardMovesGenerator.attackedByWhite(  wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc)&bkc)!=0))){
            return -1;
        }
        if(MoveUtilities.extractFromCodedMove(move,3)==0 && MoveUtilities.extractFromCodedMove(move,4)==0){
            //Castle
            long start=MoveUtilities.extractFromCodedMove(move,1);
            if(((1L<<start)&wk)!=0){ckw=false;cqw=false;}
            if(((1L<<start)&bk)!=0){ckb=false;cqb=false;}
            if(((1L<<start)&wr &(1L<<63))!=0){ckw=false;}
            if(((1L<<start)&wr &(1L<<56))!=0){cqw=false;}
            if(((1L<<start)&br &(1L<<7))!=0){ckb=false;}
            if(((1L<<start)&br &(1L<<0))!=0){cqb=false;}
        }
         wk=wkc;
         wq=wqc;
         wn=wnc;
         wb=wbc;
         wr=wrc;
         wp=wpc;
         bk=bkc;
         bq=bqc;
         bn=bnc;
         bb=bbc;
         br=brc;
         bp=bpc;
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
        System.out.println("Castle rights:");
        System.out.println("ckw:"+ckw);
        System.out.println("cqw:"+cqw);
        System.out.println("ckb:"+ckb);
        System.out.println("cqb:"+cqb);
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
