package com.ChessAI.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ApplicationUser {
    @Id
    private Integer id;
    private String username;
    private String password;
}
