package com.ChessAI.controllers;

import com.ChessAI.dto.UserDTO;
import com.ChessAI.exceptions.UserControllerException.UserAlreadyExistsException;
import com.ChessAI.exceptions.UserControllerException.UserNotFoundException;
import com.ChessAI.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService service;


    @PostMapping("/register")
    public UserDTO register(@RequestBody @Valid UserDTO user) {
        if (service.userExists(user)) {
            throw new UserAlreadyExistsException();
        }
        return service.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody @Valid UserDTO user) {
        if (!service.userExists(user)) {
            throw new UserNotFoundException();
        }
        return service.verify(user);
    }
}
