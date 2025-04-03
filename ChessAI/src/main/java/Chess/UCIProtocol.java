package Chess;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UCIProtocol {

    BitBoard board;
    int depth=11;
    int earlyGameMaxTimePerMove=40000;
    int lateGameMaxTimePerMove=20000;
    public UCIProtocol(){
        board=BitBoard.createBoardFromFen(BitBoard.defaultFen);
    }
    public UCIProtocol(int timePerMove){
        board=BitBoard.createBoardFromFen(BitBoard.defaultFen);
        this.earlyGameMaxTimePerMove=timePerMove;
        this.lateGameMaxTimePerMove=timePerMove;
    }
    public UCIProtocol(int earlyGameMaxTimePerMove,int lateGameMaxTimePerMove){
        board=BitBoard.createBoardFromFen(BitBoard.defaultFen);
        this.earlyGameMaxTimePerMove=earlyGameMaxTimePerMove;
        this.lateGameMaxTimePerMove=lateGameMaxTimePerMove;
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
                board.getAiBot().resetHistoryPly();
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
    public String getBestMove(){
        int move=board.getBestMoveIterativeDeepening(depth,earlyGameMaxTimePerMove,lateGameMaxTimePerMove);
        System.out.println("bestmove "+BitBoard.toAlgebra(move));
        return BitBoard.toAlgebra(move);
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
            board.getAiBot().resetHistoryPly();

            for(String move: moves){
                int moveCode=board.algebraToCode(move);
                //board.aiBot.historySet.clear();
                board.makeAMove(moveCode);
                long hash=board.getBoardHash();
                //board.getAiBot().history[board.getAiBot().getHistoryPly()]=hash;
                board.getAiBot().updateHistory(board.getAiBot().getHistoryPly(),hash);
                board.getAiBot().incrementHistoryPly();
                //board.aiBot.historySet.add(hash);
            }

        }
    }

    public void processInitialCommand(){
        System.out.println("id name bezpoleznik");
        System.out.println("id author bezpoleznik");
        System.out.println("uciok");

        //Reset history of the AI bot
        board.getAiBot().resetHistoryPly();
    }
    public void playWithHimself(){
        //TODO:Use string builder
        String input="position startpos";
        processPositionCommand(input);
        String currentMove=getBestMove();
        input="position startpos moves "+currentMove;
        BigDecimal time=new BigDecimal(0);
        List<String>moveTimes=new ArrayList<>();
        while(!"a1a1".equals(currentMove)){
            processPositionCommand(input);
            System.out.println(input);
            BigDecimal start= new BigDecimal( System.currentTimeMillis());
            currentMove=getBestMove();
            BigDecimal end= new BigDecimal( System.currentTimeMillis());
            BigDecimal secs=end.subtract(start).divide(new BigDecimal(1000));
            System.out.println("Move time:"+secs);
            moveTimes.add(secs.toString());
            time=time.add(secs);
            input=input + " " + currentMove;
        }
    }
}
