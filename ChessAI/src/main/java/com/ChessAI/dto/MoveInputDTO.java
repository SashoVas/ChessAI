package com.ChessAI.dto;

public class MoveInputDTO {
    public String move;
    public Integer roomId;

    public void setMove(String move) {
        this.move = move;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public String getMove() {
        return move;
    }
}
