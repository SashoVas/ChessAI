package com.ChessAI.controllers;

import com.ChessAI.dto.CreateGameDTO;
import com.ChessAI.dto.GameResultDTO;
import com.ChessAI.models.Game;
import com.ChessAI.services.GameService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@RestController
public class GameController {
    @Autowired
    private GameService gameService;

    @PostMapping("/createGame")
    public ResponseEntity<Game> createGame(@RequestBody @Valid CreateGameDTO createGameDTO,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        Game game = gameService.createGame(createGameDTO, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(game);
    }

    @GetMapping("/getFreeRooms")
    public Set<Game> getFreeRooms() {
        return gameService.getFreeRooms();
    }

    @PatchMapping("game/{roomId}")
    public GameResultDTO joinRoom(@PathVariable String roomId, @AuthenticationPrincipal UserDetails userDetails){
        return gameService.joinRoom(roomId,userDetails.getUsername());
    }

}
