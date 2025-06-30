package com.ChessAI.Chess;

public class MainChess {
    public static void main(String[] args) {

        BitBoard board=BitBoard.createBoardFromFen(BitBoard.defaultFen);

        board.printBoard();
        System.out.println(board.getFen());
        System.out.println(BitBoard.defaultFen.equals(board.getFen()));
    }

}
