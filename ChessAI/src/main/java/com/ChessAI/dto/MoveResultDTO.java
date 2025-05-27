package com.ChessAI.dto;

import com.ChessAI.models.GameStatus;
import com.ChessAI.models.PlayerColor;

import java.util.List;

public class MoveResultDTO {
    public PlayerColor getColorOfRequestUser() {
        return colorOfRequestUser;
    }

    public void setColorOfRequestUser(PlayerColor colorOfRequestUser) {
        this.colorOfRequestUser = colorOfRequestUser;
    }

    public MoveResultDTO(String fen, String move, List<String> nextMoves, GameStatus gameState, PlayerColor currentColor,PlayerColor colorOfRequestUser) {
        this.fen = fen;
        this.move = move;
        this.nextMoves = nextMoves;
        this.gameState=gameState;
        this.currentColor=currentColor;
        this.colorOfRequestUser=colorOfRequestUser;
    }

    String fen;
    String move;
    List<String> nextMoves;
    GameStatus gameState;
    PlayerColor currentColor;
    PlayerColor colorOfRequestUser;

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

    public GameStatus getGameState() {
        return gameState;
    }

    public void setGameState(GameStatus gameState) {
        this.gameState = gameState;
    }

    public PlayerColor getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(PlayerColor currentColor) {
        this.currentColor = currentColor;
    }
}
