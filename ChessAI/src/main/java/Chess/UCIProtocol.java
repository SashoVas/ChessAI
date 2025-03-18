package Chess;

import java.util.List;
import java.util.Scanner;

public class UCIProtocol {

    BitBoard board;
    int depth=8;
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
                //board.printBoard();
            }
            else if(line.contains("go")){
                getBestMove();
            }
            else if("print".equals(line)){
                board.printBoard();
            }
            else if("quit".equals(line)){
                break;
            }
        }
    }
    public void getBestMove(){
        int move=board.getBestMoveIterativeDeepening(depth);
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
            AIBot.historyPly=0;

            for(String move: moves){
                int moveCode=board.algebraToCode(move);
                board.makeAMove(moveCode);
                AIBot.history[AIBot.historyPly]=board.getBoardHash();
                AIBot.historyPly++;
            }

        }
    }

    public void processInitialCommand(){
        System.out.println("id name bezpoleznik");
        System.out.println("id author bezpoleznik");
        System.out.println("uciok");

        //Reset history of the AI bot
        AIBot.historyPly=0;
    }
}
