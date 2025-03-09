package Chess;

import java.util.List;
import java.util.Scanner;

public class UCIProtocol {

    BitBoard board;
    int depth=6;
    public UCIProtocol(){
        board=BitBoard.createBoardFromFen(BitBoard.defaultFen);
    }
    public void startGame(){
        Scanner input=new Scanner(System.in);
        while(true){
            String line=input.nextLine();
            if("uci".equals(line)){
                processInitialCommand();
            }
            else if(line.startsWith("setoption")){

            }
            else if ("isready".equals(line)) {
                System.out.println("readyok");
            }
            else if("ucinewgame".equals(line)){
                board=BitBoard.createBoardFromFen(BitBoard.defaultFen);
            }
            else if(line.startsWith("position")){
                processPositionCommand(line);
            }
            else if(line.contains("go")){
                getBestMove();
            }
            else if("print".equals(line)){
                board.printBoard();
            }
        }
    }
    public void getBestMove(){
        long move=board.getBestMove(depth);
        System.out.println("bestmove "+BitBoard.toAlgebra(move));
    }
    public void processPositionCommand(String line){
        line=line.substring(9);

        if(line.contains("startpos")){
            board=BitBoard.createBoardFromFen(BitBoard.defaultFen);
            if(line.length()>8){
                line=line.substring(9);

            }
        }
        else if(line.contains("fen")){
            line=line.substring(4);
            board=BitBoard.createBoardFromFen(line);
        }
        if(line.contains("moves")){
            line=line.substring(line.indexOf("moves")+6);
            String[] moves=line.split(" ");

            for(String move: moves){
                long moveCode=board.algebraToCode(move);
                board.makeAMove(moveCode);
            }
            //while(line.length()>0){
            //    String move=line.substring(0,5);
            //    line=line.substring(5);
            //    long moveCode=board.algebraToCode(move);
            //    board.makeAMove(moveCode);
            //}

        }
    }

    public void processInitialCommand(){
        System.out.println("id name bezpoleznik");
        System.out.println("id author bezpoleznik");
        System.out.println("uciok");
    }
}
