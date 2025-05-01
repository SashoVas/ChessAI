package com.ChessAI.controllers;

import com.ChessAI.dto.MoveDTO;
import com.ChessAI.services.MoveService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games/{gameId}/moves")
public class MoveController {
    @Autowired
    private MoveService moveService;

    @PostMapping
    public void makeMove(@RequestBody @Valid MoveDTO move, @PathVariable Integer gameId) {
        moveService.makeMove(move, gameId);
    }
}
