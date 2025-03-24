
import Chess.*;
import Chess.StandardImplementation.Pieces.Board;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        //UCIProtocol game=new UCIProtocol();
        //game.startGame();
        //BitBoard board=BitBoard.createBoardFromFen(BitBoard.defaultFen);
        //System.out.println(board.perft(6));

        //BitBoard board=BitBoard.createBoardFromFen("8/8/1p6/8/2P5/p1K5/k2R2bp/8 b - - 5 55 ");
        //System.out.println(board.evaluate());
        //for(int i=0;i<63;i++){
        //    BitBoardMovesGenerator.printMask(AIBot.passedPawnMasksBlack[i]);
        //    System.out.println("==========");
        //}
        BitBoard board=BitBoard.createBoardFromFen(BitBoard.defaultFen);
        //BitBoard board=BitBoard.createBoardFromFen("6k1/pp6/1p4pp/8/8/8/3q4/K7 b - - 3 41 ");
        //System.out.println(board.evaluate());
        //int move=board.getBestMoveIterativeDeepening(8);

       int move=board.getBestMoveIterativeDeepening(10);
       System.out.println(board.getBoardHash());
       System.out.println(MoveUtilities.extractStart(move));
       System.out.println(MoveUtilities.extractEnd(move));
       System.out.println(AIBot.nodes);
       System.out.println(BitBoard.toAlgebra(move));
       board.makeAMove(move);
       System.out.println(board.evaluate());


    }
}