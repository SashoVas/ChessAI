package com.ChessAI.controllers;

import com.ChessAI.dto.UserDTO;
import com.ChessAI.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService service;


    @PostMapping("/register")
    public UserDTO register(@RequestBody UserDTO user) {
        return service.register(user);

    }

    @PostMapping("/login")
    public String login(@RequestBody UserDTO user) {

        return service.verify(user);
    }
}
