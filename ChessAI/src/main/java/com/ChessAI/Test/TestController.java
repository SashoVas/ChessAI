package com.ChessAI.Test;

import com.ChessAI.Chess.BitBoard;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test(@RequestParam String fen){
        BitBoard bitboard=BitBoard.createBoardFromFen(fen);
        int hashMove=bitboard.getBestMoveIterativeDeepening(6,1,1);
        String move=BitBoard.toAlgebra(hashMove);
        return move;
    }
}
