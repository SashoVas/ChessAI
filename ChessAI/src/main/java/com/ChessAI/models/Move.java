package com.ChessAI.models;

import jakarta.persistence.*;

@Entity
@Table(name = "moves")
public class Move {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private Integer GameId;
    private String initialFen;
    private String finalFen;
    private String moveNr;
}
