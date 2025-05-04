package com.ChessAI.models;

import jakarta.persistence.*;

import com.ChessAI.dto.UserDTO;
import jakarta.validation.constraints.NotNull;


@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "user_id")
    private Integer id;
    @Column(name = "elo_rating")
    @NotNull
    private Integer elo = 1500; //TODO: get from config
    @Column(name = "is_elo_provisional")
    private Boolean isEloProvisional = true;
    private String username;
    private String password;
    private String email;

    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User(UserDTO userDTO) {
        this.username = userDTO.getUsername();
        this.password = userDTO.getPassword();
        this.email = userDTO.getEmail();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getEloRating() {
        return elo;
    }

    public void setEloRating(Integer elo) {
        this.elo = elo;
    }

    public Boolean IsEloProvisional() {
        return isEloProvisional;
    }

    public void setIsEloProvisional(Boolean isEloProvisional) {
        this.isEloProvisional = isEloProvisional;
    }
}