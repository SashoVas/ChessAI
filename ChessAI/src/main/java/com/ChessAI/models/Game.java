package com.ChessAI.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    //TODO:Make gameId string
    private Integer gameId;

    //Game creator is user1
    @ManyToOne
    @JoinColumn(name = "user1_id", referencedColumnName = "user_id")
    private User user1 = null;

    //Remains null if game is against chess bot
    @ManyToOne
    @JoinColumn(name = "user2_id", referencedColumnName = "user_id")
    private User user2 = null;

    private String currentFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Move> moves = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "user1_color")
    private PlayerColor user1Color = PlayerColor.UNKNOWN;

    @Enumerated(EnumType.STRING)
    @Column(name = "user2_color")
    private PlayerColor user2Color = PlayerColor.UNKNOWN;

    //TODO: See if there is a better way to handle time left
    @Column(name = "user1_time_left")
    private Integer user1TimeLeft = -1;

    @Column(name = "user2_time_left")
    private Integer user2TimeLeft = -1;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_type")
    private GameType gameType = GameType.UNKNOWN;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_status")
    private GameStatus gameStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = "current_turn_color")
    private PlayerColor currentTurnColor;

    @Column(name = "game_time_seconds")
    private Integer gameTimeSeconds = -1;

    @Column(name = "currentTurn")
    private Integer currentTurn = 0;

    public Game() {}

    public Game(User user1, User user2, PlayerColor user1Color, PlayerColor user2Color, Integer user1TimeLeft, Integer user2TimeLeft, GameType gameType, Integer gameTimeSeconds) {
        this.user1 = user1;
        this.user2 = user2;
        this.user1Color = user1Color;
        this.user2Color = user2Color;
        this.user1TimeLeft = user1TimeLeft;
        this.user2TimeLeft = user2TimeLeft;
        this.gameType = gameType;
        this.gameTimeSeconds = gameTimeSeconds;
    }
    public Integer getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(Integer currentTurn) {
        this.currentTurn = currentTurn;
    }
    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public String getCurrentFen() {
        return currentFen;
    }

    public void setCurrentFen(String currentFen) {
        this.currentFen = currentFen;
    }

    public Set<Move> getMoves() {
        return moves;
    }

    public void setMoves(Set<Move> moves) {
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

    public Integer getGameTimeSeconds() {
        return gameTimeSeconds;
    }

    public void setGameTimeSeconds(Integer gameTimeSeconds) {
        this.gameTimeSeconds = gameTimeSeconds;
    }

    public PlayerColor getCurrentTurnColor() {
        return currentTurnColor;
    }

    public void setCurrentTurnColor(PlayerColor currentTurnColor) {
        this.currentTurnColor = currentTurnColor;
    }
}