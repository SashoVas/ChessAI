package com.ChessAI.controllers;

import com.ChessAI.dto.UserDTO;
import com.ChessAI.dto.UserLoginDTO;
import com.ChessAI.exceptions.UserException.UserAlreadyExistsException;
import com.ChessAI.exceptions.UserException.UserNotFoundException;
import com.ChessAI.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService service;

    //tested by postman collection
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody @Valid UserDTO user) {
        if (service.userExists(user)) {
            throw new UserAlreadyExistsException();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(user));
    }

    //tested by postman collection
    @PostMapping("/login")
    public ResponseEntity<UserLoginDTO> login(@RequestBody @Valid UserDTO user) {
        if (!service.userExists(user)) {
            throw new UserNotFoundException();
        }
        return ResponseEntity.status(HttpStatus.OK).body(service.verify(user));
    }
}
