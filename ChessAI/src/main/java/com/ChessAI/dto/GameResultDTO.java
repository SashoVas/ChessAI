package com.ChessAI.dto;

import com.ChessAI.models.*;

import java.util.Comparator;
import java.util.List;

public class GameResultDTO {
    private Integer gameId;
    private String user1Username ;
    private String user2Username;
    private String currentFen;
    private List<String> moves ;
    private PlayerColor user1Color ;
    private PlayerColor user2Color ;
    private Integer user1TimeLeft;
    private Integer user2TimeLeft;
    private GameType gameType ;
    private GameStatus gameStatus;
    private PlayerColor currentTurnColor;
    private Integer gameTimeSeconds;
    private Integer currentTurn ;
    private Integer user1Rating;
    private Integer user2Rating;
    private Boolean user1IsEloProvisional;
    private Boolean user2IsEloProvisional;
    private java.time.LocalDateTime createdAt;

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public String getUser1Username() {
        return user1Username;
    }

    public void setUser1Username(String user1Username) {
        this.user1Username = user1Username;
    }

    public String getUser2Username() {
        return user2Username;
    }

    public void setUser2Username(String user2Username) {
        this.user2Username = user2Username;
    }

    public String getCurrentFen() {
        return currentFen;
    }

    public void setCurrentFen(String currentFen) {
        this.currentFen = currentFen;
    }

    public List<String> getMoves() {
        return moves;
    }

    public void setMoves(List<String> moves) {
        this.moves = moves;
    }

    public PlayerColor getUser1Color() {
        return user1Color;
    }

    public void setUser1Color(PlayerColor user1Color) {
        this.user1Color = user1Color;
    }

    public PlayerColor getUser2Color() {
        return user2Color;
    }

    public void setUser2Color(PlayerColor user2Color) {
        this.user2Color = user2Color;
    }

    public Integer getUser1TimeLeft() {
        return user1TimeLeft;
    }

    public void setUser1TimeLeft(Integer user1TimeLeft) {
        this.user1TimeLeft = user1TimeLeft;
    }

    public Integer getUser2TimeLeft() {
        return user2TimeLeft;
    }

    public void setUser2TimeLeft(Integer user2TimeLeft) {
        this.user2TimeLeft = user2TimeLeft;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public PlayerColor getCurrentTurnColor() {
        return currentTurnColor;
    }

    public void setCurrentTurnColor(PlayerColor currentTurnColor) {
        this.currentTurnColor = currentTurnColor;
    }

    public Integer getGameTimeSeconds() {
        return gameTimeSeconds;
    }

    public void setGameTimeSeconds(Integer gameTimeSeconds) {
        this.gameTimeSeconds = gameTimeSeconds;
    }

    public Integer getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(Integer currentTurn) {
        this.currentTurn = currentTurn;
    }

    public Integer getUser1Rating() {
        return user1Rating;
    }

    public void setUser1Rating(Integer user1Rating) {
        this.user1Rating = user1Rating;
    }

    public Integer getUser2Rating() {
        return user2Rating;
    }

    public void setUser2Rating(Integer user2Rating) {
        this.user2Rating = user2Rating;
    }

    public Boolean getUser1IsEloProvisional() {
        return user1IsEloProvisional;
    }

    public void setUser1IsEloProvisional(Boolean user1IsEloProvisional) {
        this.user1IsEloProvisional = user1IsEloProvisional;
    }

    public Boolean getUser2IsEloProvisional() {
        return user2IsEloProvisional;
    }

    public void setUser2IsEloProvisional(Boolean user2IsEloProvisional) {
        this.user2IsEloProvisional = user2IsEloProvisional;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static GameResultDTO fromEntity(Game game){
        GameResultDTO gameDTO=new GameResultDTO();
        gameDTO.setGameId(game.getGameId());
        if (game.getUser1()!=null) {
            gameDTO.setUser1Username(game.getUser1().getUsername());
            gameDTO.setUser1Rating(game.getUser1Elo());
            gameDTO.setUser1IsEloProvisional(game.getIsUser1EloProvisional());
        }
        if (game.getUser2()!=null) {
            gameDTO.setUser2Username(game.getUser2().getUsername());
            gameDTO.setUser2Rating(game.getUser2Elo());
            gameDTO.setUser2IsEloProvisional(game.getIsUser2EloProvisional());
        }
        gameDTO.setUser1Color(game.getUser1Color());
        gameDTO.setUser2Color(game.getUser2Color());

        gameDTO.setMoves(
                game.getMoves().stream()
                        .sorted(Comparator.comparing(Move::getTurn))
                        .map(Move::getMoveNr)
                        .toList());

        gameDTO.setCurrentFen(game.getCurrentFen());
        gameDTO.setUser1TimeLeft(game.getUser1TimeLeft());
        gameDTO.setUser2TimeLeft(game.getUser2TimeLeft());
        gameDTO.setGameStatus(game.getGameStatus() != null ? game.getGameStatus() : GameStatus.NOT_STARTED);
        gameDTO.setCurrentTurn(game.getCurrentTurn());
        gameDTO.setGameTimeSeconds(game.getGameTimeSeconds());
        gameDTO.setCurrentTurnColor(game.getCurrentTurnColor());
        gameDTO.setGameType(game.getGameType());
        gameDTO.setCreatedAt(game.getCreatedAt());
        return gameDTO;
    }
}
