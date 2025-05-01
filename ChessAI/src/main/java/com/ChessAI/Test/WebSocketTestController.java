package com.ChessAI.Test;

import com.ChessAI.Chess.BitBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller

public class WebSocketTestController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    String currentFen;//TODO:Take from db
    @MessageMapping("/game.makeMove")
    public void move(@Payload Move move) {
        System.out.println(move.move);
        System.out.println(move.roomId);

        BitBoard bitboard=BitBoard.createBoardFromFen(currentFen);
        int decodedMove= bitboard.algebraToCode(move.move);
        bitboard.makeAMove(decodedMove);
        currentFen=bitboard.getFen();
        int hashMove=bitboard.getBestMoveIterativeDeepening(6,1,1);
        String nextMove=BitBoard.toAlgebra(hashMove);
        bitboard.makeAMove(hashMove);
        currentFen=bitboard.getFen();

        String destination = "/room/game." + move.roomId;
        MoveResult result=new MoveResult(currentFen,nextMove,bitboard.getPossibleNextMoves());
        messagingTemplate.convertAndSend(destination, result);
    }
}
