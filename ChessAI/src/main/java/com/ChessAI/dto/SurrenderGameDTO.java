package com.ChessAI.dto;

public class SurrenderGameDTO {
    public SurrenderGameDTO(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    String roomId;
}
