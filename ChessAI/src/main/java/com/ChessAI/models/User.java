package com.ChessAI.models;

import jakarta.persistence.*;

import com.ChessAI.dto.UserDTO;


@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "user_id")
    private Integer id;
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
}