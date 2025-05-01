package com.ChessAI.controllers;

import com.ChessAI.dto.CreateGameDTO;
import com.ChessAI.models.Game;
import com.ChessAI.services.GameService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;


@RestController
public class GameController {
    @Autowired
    private GameService gameService;

    @PostMapping("/createGame")
    public Game createGame(@RequestBody @Valid CreateGameDTO createGameDTO,
                           @AuthenticationPrincipal UserDetails userDetails) {
        return gameService.createGame(createGameDTO, userDetails);
    }

    @GetMapping("/getFreeRooms")
    public Set<Game> getFreeRooms() {
        return gameService.getFreeRooms();
    }

}
