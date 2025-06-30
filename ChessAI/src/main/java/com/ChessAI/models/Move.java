package com.ChessAI.models;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "moves")
public class Move {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;
    private String initialFen;
    private String finalFen;
    private String moveNr;
    private Integer turn;
    public Move(){};

    public Move(Integer id, Game game, String initialFen, String finalFen, String moveNr) {
        this.id = id;
        this.game = game;
        this.initialFen = initialFen;
        this.finalFen = finalFen;
        this.moveNr = moveNr;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getInitialFen() {
        return initialFen;
    }

    public void setInitialFen(String initialFen) {
        this.initialFen = initialFen;
    }

    public String getFinalFen() {
        return finalFen;
    }

    public void setFinalFen(String finalFen) {
        this.finalFen = finalFen;
    }

    public String getMoveNr() {
        return moveNr;
    }

    public void setMoveNr(String moveNr) {
        this.moveNr = moveNr;
    }

    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return Objects.equals(id, move.id) && Objects.equals(game, move.game) && Objects.equals(initialFen, move.initialFen) && Objects.equals(finalFen, move.finalFen) && Objects.equals(moveNr, move.moveNr) && Objects.equals(turn, move.turn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, game, initialFen, finalFen, moveNr, turn);
    }
}
