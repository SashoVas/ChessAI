package com.ChessAI.models;

import jakarta.persistence.*;

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
}
