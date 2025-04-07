package com.ChessAI.Test;

public class Move {
    public String move;
    public String roomId;

    public void setMove(String move) {
        this.move = move;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getMove() {
        return move;
    }
}
