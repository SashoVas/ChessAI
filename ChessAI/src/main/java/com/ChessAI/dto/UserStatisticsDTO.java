package com.ChessAI.dto;

public class UserStatisticsDTO {
    private String username;
    private Integer eloRating;
    private Boolean isEloProvisional;
    private Integer totalGames;
    private Integer wins;
    private Integer losses;
    private Integer draws;
    private Double winRate;
    private Integer lastGameId;
    private String lastGameResult;
    private String lastGameOpponent;

    public UserStatisticsDTO() {}

    public UserStatisticsDTO(String username, Integer eloRating, Boolean isEloProvisional, 
                            Integer totalGames, Integer wins, Integer losses, Integer draws,
                            Integer lastGameId, String lastGameResult, String lastGameOpponent) {
        this.username = username;
        this.eloRating = eloRating;
        this.isEloProvisional = isEloProvisional;
        this.totalGames = totalGames;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
        this.winRate = totalGames > 0 ? (double) wins / totalGames * 100 : 0.0;
        this.lastGameId = lastGameId;
        this.lastGameResult = lastGameResult;
        this.lastGameOpponent = lastGameOpponent;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getEloRating() {
        return eloRating;
    }

    public void setEloRating(Integer eloRating) {
        this.eloRating = eloRating;
    }

    public Boolean getIsEloProvisional() {
        return isEloProvisional;
    }

    public void setIsEloProvisional(Boolean isEloProvisional) {
        this.isEloProvisional = isEloProvisional;
    }

    public Integer getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(Integer totalGames) {
        this.totalGames = totalGames;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getLosses() {
        return losses;
    }

    public void setLosses(Integer losses) {
        this.losses = losses;
    }

    public Integer getDraws() {
        return draws;
    }

    public void setDraws(Integer draws) {
        this.draws = draws;
    }

    public Double getWinRate() {
        return winRate;
    }

    public void setWinRate(Double winRate) {
        this.winRate = winRate;
    }

    public Integer getLastGameId() {
        return lastGameId;
    }

    public void setLastGameId(Integer lastGameId) {
        this.lastGameId = lastGameId;
    }

    public String getLastGameResult() {
        return lastGameResult;
    }

    public void setLastGameResult(String lastGameResult) {
        this.lastGameResult = lastGameResult;
    }

    public String getLastGameOpponent() {
        return lastGameOpponent;
    }

    public void setLastGameOpponent(String lastGameOpponent) {
        this.lastGameOpponent = lastGameOpponent;
    }
} 