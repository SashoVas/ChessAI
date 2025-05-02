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
}
