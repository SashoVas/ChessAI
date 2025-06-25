package com.ChessAI.controllers;

import com.ChessAI.dto.CreateGameDTO;
import com.ChessAI.dto.GameResultDTO;
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

    @PostMapping("/games")
    public ResponseEntity<GameResultDTO> createGame(@RequestBody @Valid CreateGameDTO createGameDTO,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        GameResultDTO game = gameService.createGame(createGameDTO, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(game);
    }

    @GetMapping("/games")
    public Set<GameResultDTO> getGames(@RequestParam(required = false, defaultValue = "false") boolean free) {
        if (free) {
            return gameService.getFreeRooms();
        }
        //TODO: implement logic to return all games if needed
        return null;
    }

    @GetMapping("/games/{roomId}")
    public GameResultDTO getGame(@PathVariable String roomId){
        return gameService.getGameState(roomId);
    }

    @PostMapping("/games/{roomId}")
    public GameResultDTO joinRoom(@PathVariable String roomId, @AuthenticationPrincipal UserDetails userDetails){
        return gameService.joinRoom(roomId,userDetails.getUsername());
    }

}
