package com.ChessAI.Test;

import com.ChessAI.Chess.BitBoard;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test(Authentication authentication, @RequestParam String fen){
        String username = authentication.getName();
        System.out.println("Request from "+username);
        BitBoard bitboard=BitBoard.createBoardFromFen(fen);
        int hashMove=bitboard.getBestMoveIterativeDeepening(6,1,1);
        String move=BitBoard.toAlgebra(hashMove);
        return move;
    }
}
