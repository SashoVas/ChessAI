package com.ChessAI.Chess;

import com.ChessAI.ChessAiApplication;
import org.springframework.boot.SpringApplication;

public class MainChess {
    public static void main(String[] args) {

        BitBoard board=BitBoard.createBoardFromFen(BitBoard.defaultFen);

        board.printBoard();
        System.out.println(board.getFen());
        System.out.println(BitBoard.defaultFen.equals(board.getFen()));
    }

}
