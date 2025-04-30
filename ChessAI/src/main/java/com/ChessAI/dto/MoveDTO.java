package com.ChessAI.dto;

import jakarta.validation.constraints.Pattern;

public class MoveDTO {
    @Pattern(regexp = "^[a-h][1-8]$", message = "Invalid 'from' position format")
    private String from;
    @Pattern(regexp = "^[a-h][1-8]$", message = "Invalid 'to' position format")
    private String to;

    public MoveDTO() {
    }

    public MoveDTO(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
