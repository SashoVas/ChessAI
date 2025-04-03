
import Chess.*;
import Chess.Moves.MoveUtilities;
import Chess.StandardImplementation.Pieces.Board;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        UCIProtocol game=new UCIProtocol();
        //game.playWithHimself();
        game.startGame();
        //BitBoard board=BitBoard.createBoardFromFen("5rk1/2pbb1pp/p7/2PP1pq1/2BB4/5P2/PP2QP1P/R4RK1 w - - 1 25 ");
        //System.out.println(board.perft(6));

        //BitBoard board=BitBoard.createBoardFromFen("8/5p2/r7/6R1/3k4/6KP/5P2/8 b - - 2 56 ");
        //BitBoard board=BitBoard.createBoardFromFen(BitBoard.defaultFen);

        //System.out.println(board.evaluate());
        //for(int i=0;i<63;i++){
        //    BitBoardMovesGenerator.printMask(AIBot.passedPawnMasksBlack[i]);
        //    System.out.println("==========");
        //}
        //BitBoard board=BitBoard.createBoardFromFen(BitBoard.defaultFen);
        //BitBoard board=BitBoard.createBoardFromFen("6k1/pp6/1p4pp/8/8/8/3q4/K7 b - - 3 41 ");
        //System.out.println(board.evaluate());
        //int move=board.getBestMoveIterativeDeepening(8);

        //long start=System.currentTimeMillis();
        //int move=board.getBestMoveIterativeDeepening(11);
        //long end=System.currentTimeMillis();
        //System.out.println(board.getBoardHash());
        //System.out.println(BitBoard.toAlgebra(move));
        //board.makeAMove(move);
        //System.out.println(board.evaluate());
        //System.out.println(((end-start)/1000)+" Sec.");

    }
}