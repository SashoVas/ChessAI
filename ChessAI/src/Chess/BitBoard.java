package Chess;

import Chess.Pieces.*;
import Chess.Pieces.Base.Piece;

public class BitBoard {
    public static final String defaultFen="rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

    long wk,wq,wn,wb,wr,wp=0L;
    long bk,bq,bn,bb,br,bp=0L;
    private BitBoard(){

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
