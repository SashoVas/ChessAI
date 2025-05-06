package com.ChessAI.Chess;

import com.ChessAI.Chess.Evaluation.BoardEvaluation;
import com.ChessAI.Chess.Moves.BitBoardMovesGenerator;
import com.ChessAI.Chess.Moves.MoveUtilities;
import com.ChessAI.Chess.TranspositionTable.ZobristHash;
import com.ChessAI.models.GameStatus;

import java.util.List;

public class BitBoard {
    public static final String defaultFen="rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private long wk,wq,wn,wb,wr,wp=0L;
    private long bk,bq,bn,bb,br,bp=0L;
    private boolean ckw=true;
    private boolean cqw=true;
    private boolean ckb=true;
    private boolean cqb=true;
    private int currentTurn=1;//1-White,0-Black
    private int lastMove=0;
    private AIBot aiBot;


    private BitBoard(){
        for (int i = 7; i >= 0; i--) {
            BitBoardMovesGenerator.FILE_MASKS[i] = 0x0101010101010101L << i;
            BitBoardMovesGenerator.RANK_MASKS[7-i] = 0xFFL << (8 * i);
        }
        BitBoardMovesGenerator.movesOverTheBoardMaskRight=~(BitBoardMovesGenerator.FILE_MASKS[6]|BitBoardMovesGenerator.FILE_MASKS[7]);
        BitBoardMovesGenerator.movesOverTheBoardMaskLeft=~(BitBoardMovesGenerator.FILE_MASKS[0]|BitBoardMovesGenerator.FILE_MASKS[1]);
        aiBot=new AIBot(40000,20000);
        ZobristHash.initializeHashes();
        BoardEvaluation.generatePawnMasks();
        aiBot.getTranspositionTable().clear();
        //aiBot.historySet.clear();
    }

    public GameStatus getState(){
        if (!getPossibleNextMoves().isEmpty()){
            return GameStatus.IN_PROGRESS;
        }
        if(isTie()){
            return GameStatus.DRAW;
        }
        if (currentTurn==1) return GameStatus.WINNER_BLACK;
        return GameStatus.WINNER_WHITE;
    }
    public boolean isTie(){
        return getPossibleNextMoves().isEmpty() && !isInCheck();
    }
    public boolean isInCheck(){
        return (currentTurn==1 && ((BitBoardMovesGenerator.attackedByBlack(  wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp)&wk)!=0))||
                (currentTurn==0&& ((BitBoardMovesGenerator.attackedByWhite(  wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp)&bk)!=0));
    }

    public boolean isMoveLegal(int move){
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
        if((currentTurn==1 && ((BitBoardMovesGenerator.attackedByBlack(  wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc)&wkc)!=0))||
                (currentTurn==0&& ((BitBoardMovesGenerator.attackedByWhite(  wkc, wqc, wnc, wbc, wrc, wpc, bkc, bqc, bnc, bbc, brc, bpc)&bkc)!=0))){
            return false;
        }
        return true;
    }
    private void bitBoardToArray(String[][]board,String initial,long bitBoard){
        long i=bitBoard& -bitBoard;

        while(i!=0){
            int index=Long.numberOfTrailingZeros(i);

            int row=index/8;
            int col=index%8;
            board[row][col]=initial;
            bitBoard&=~i;
            i=bitBoard& -bitBoard;
        }
    }
    private String parseBoardToFen(){
        String[][]board=new String[8][8];
        bitBoardToArray(board,"K",wk);
        bitBoardToArray(board,"Q",wq);
        bitBoardToArray(board,"R",wr);
        bitBoardToArray(board,"B",wb);
        bitBoardToArray(board,"N",wn);
        bitBoardToArray(board,"P",wp);

        bitBoardToArray(board,"k",bk);
        bitBoardToArray(board,"q",bq);
        bitBoardToArray(board,"r",br);
        bitBoardToArray(board,"b",bb);
        bitBoardToArray(board,"n",bn);
        bitBoardToArray(board,"p",bp);
        String result="";
        for(int i=0;i<8;i++){
            int counter=0;
            for (int j=0;j<8;j++){
                if(board[i][j]==null){
                    counter++;
                }
                else
                {
                    if (counter!=0)
                        result+=counter;
                    result+=board[i][j];
                    counter=0;
                }
            }
            if(counter!=0)
                result+=counter;
            if(i!=7)
                result+="/";

        }
        return result;
    }
    public String getFen(){
        String result=parseBoardToFen();

        //Turn
        if (isWhiteOnTurn())
            result+=" w ";
        else
            result+=" b ";
        //Castle rights
        if(ckw)
            result+="K";
        if (cqw)
            result+="Q";
        if(ckb)
            result+="k";
        if(cqb)
            result+="q";
        if(!(ckw|cqw|ckb|cqb))
            result+="-";
        //EnPassant
        if (MoveUtilities.isEnPassant(lastMove))
            result+=toAlgebra(lastMove);
        else
            result+=" - ";

        //Half and Full move clock
        result+="0 1";

        return result;
    }
    public boolean isWhiteOnTurn(){
        return currentTurn==1;
    }
    public List<String>getPossibleNextMoves(){
        List<Integer> nextMoves;
        if (isWhiteOnTurn()) nextMoves=generateMovesW();
        else nextMoves=generateMovesB();
        return nextMoves
                .stream()
                .filter(this::isMoveLegal)
                .map(BitBoard::toAlgebra)
                .toList();
    }
    public AIBot getAiBot(){
        return aiBot;
    }
    public long perftWithUndo(int depth){
        long[]arr={wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp};
        return BitBoardMovesGenerator.perftWithUndo(arr,ckw,cqw,ckb,cqb,depth,currentTurn,lastMove);
    }
    public long getBoardHash(){
        return ZobristHash.hashBoard(wk,wq,wn,wb,wr,wp,bk,bq,bn,bb,br,bp,ckw,cqw,ckb,cqb,currentTurn,lastMove);
    }
    public static String toAlgebra(int move){
        String result="";
        long start= MoveUtilities.extractStart(move);
        long end=MoveUtilities.extractEnd(move);
        long promotion=MoveUtilities.extractPromotion(move);
        result=""+(char)('a'+start%8)+(8-start/8)+(char)('a'+end%8)+(8-end/8);
        result+=(promotion==0?"":MoveUtilities.promotionToAlgebra((int)promotion));
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
            long codeStart=MoveUtilities.extractStart(moveCode);
            long codeEnd=MoveUtilities.extractEnd(moveCode);
            if(codeStart==moveStart&&codeEnd==moveEnd){
                long promotionPiece=MoveUtilities.extractPromotion(moveCode);
                if(isPromotion && promotionPiece!=0 && move.toLowerCase().charAt(4)==MoveUtilities.promotionToAlgebra((int)promotionPiece).toLowerCase().charAt(0)){
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
        aiBot.setHash(getBoardHash());
        aiBot.getTranspositionTable().clear();
        return aiBot.getBestMove(depth,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw, cqw, ckb, cqb,currentTurn,lastMove);
    }
    public int getBestMoveIterativeDeepening(int depth,int earlyGameTime,int lateGameTime){
        aiBot.setTimeControls(earlyGameTime,lateGameTime);
        aiBot.setHash(getBoardHash());
        aiBot.getTranspositionTable().clear();
        return aiBot.getBestMoveIterativeDeepening(depth,wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,ckw, cqw, ckb, cqb,currentTurn,lastMove);
    }
    public int evaluate(){
        return BoardEvaluation.evaluate(wk, wq, wn, wb, wr, wp, bk, bq, bn, bb, br, bp,currentTurn);
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
        if(!MoveUtilities.isPromotion(move) && !MoveUtilities.isEnPassant(move)){
            //Castle
            long start=MoveUtilities.extractStart(move);
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
