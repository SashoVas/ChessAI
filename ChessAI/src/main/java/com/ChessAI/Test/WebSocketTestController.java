package com.ChessAI.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller

public class WebSocketTestController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/game.makeMove")
    public void move(@Payload Move move) {
        System.out.println(move.move);
        System.out.println(move.roomId);

        String destination = "/room/game." + move.roomId;
        messagingTemplate.convertAndSend(destination, move);
    }
}
