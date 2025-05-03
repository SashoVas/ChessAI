package com.ChessAI.controllers.WebSockets;

import com.ChessAI.dto.InitialConnectDTO;
import com.ChessAI.dto.MoveInputDTO;
import com.ChessAI.dto.MoveResultDTO;
import com.ChessAI.services.MoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller

public class MoveWebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private MoveService moveService;

    @MessageMapping("/game.initialConnect")
    public void initialConnect(@Payload InitialConnectDTO input){
        MoveResultDTO result=moveService.getCurrentGameState(input);
        String destination = "/room/game." + input.getRoomId();
        messagingTemplate.convertAndSend(destination, result);
    }
    @MessageMapping("/game.makeMoveToBot")
    public void moveOnBot(@Payload MoveInputDTO move) {
        System.out.println(move.move);
        System.out.println(move.roomId);

        MoveResultDTO result=moveService.makeAMoveToBot(move);
        String destination = "/room/game." + move.roomId;
        messagingTemplate.convertAndSend(destination, result);
    }
    @MessageMapping("/game.makeMoveToPlayer")

    public void moveOnPlayer(@Payload MoveInputDTO move){
        System.out.println(move.move);
        System.out.println(move.roomId);

        MoveResultDTO result=moveService.makeAMoveToPlayer(move);

        String destination = "/room/game." + move.roomId;
        messagingTemplate.convertAndSend(destination, result);
    }
}
