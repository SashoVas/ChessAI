package com.ChessAI.controllers;

import com.ChessAI.dto.CreateGameDTO;
import com.ChessAI.dto.GameResultDTO;
import com.ChessAI.dto.JoinRoomDTO;
import com.ChessAI.models.Game;
import com.ChessAI.services.GameService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("game/{roomId}")
    public GameResultDTO joinRoom(@PathVariable String roomId, @AuthenticationPrincipal UserDetails userDetails){
        GameResultDTO result=gameService.joinRoom(roomId,userDetails.getUsername());
        return result;
    }

}
