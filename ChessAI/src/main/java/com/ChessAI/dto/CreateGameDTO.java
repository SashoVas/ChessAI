package com.ChessAI.dto;


import com.ChessAI.models.GameType;
import jakarta.validation.constraints.Min;

public class CreateGameDTO {
    private GameType gameType;
    @Min(30)
    private Integer gameTimeSeconds;

    public CreateGameDTO() {}

    public CreateGameDTO(GameType gameType, Integer gameTimeSeconds) {
        this.gameType = gameType;
        this.gameTimeSeconds = gameTimeSeconds;
    }
    public GameType getGameType() {
        return gameType;
    }
    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }
    public Integer getGameTimeSeconds() {
        return gameTimeSeconds;
    }
    public void setGameTimeSeconds(Integer gameTimeSeconds) {
        this.gameTimeSeconds = gameTimeSeconds;
    }

}
