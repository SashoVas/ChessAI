package com.ChessAI.dto;

import java.util.List;

public class MoveResultDTO {

    public MoveResultDTO(String fen, String move, List<String> nextMoves) {
        this.fen = fen;
        this.move = move;
        this.nextMoves = nextMoves;
    }

    String fen;
    String move;
    List<String> nextMoves;

    public String getFen() {
        return fen;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public List<String> getNextMoves() {
        return nextMoves;
    }

    public void setNextMoves(List<String> nextMoves) {
        this.nextMoves = nextMoves;
    }

}
