package com.ChessAI.dto;

public class MoveInputDTO {
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
