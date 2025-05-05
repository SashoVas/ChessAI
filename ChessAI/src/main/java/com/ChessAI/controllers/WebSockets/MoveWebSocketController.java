package com.ChessAI.controllers.WebSockets;

import com.ChessAI.dto.InitialConnectDTO;
import com.ChessAI.dto.MoveInputDTO;
import com.ChessAI.dto.MoveResultDTO;
import com.ChessAI.models.UserPrincipal;
import com.ChessAI.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller

public class MoveWebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private GameService gameService;

    @MessageMapping("/game.initialConnect")
    public void initialConnect(Principal principal, @Payload InitialConnectDTO input){
        String username=principal.getName();
        System.out.println(username);

        //System.out.println(userDetails.getUsername());
        MoveResultDTO result=gameService.getCurrentGameState(input);
        String destination = "/room/game." + input.getRoomId();
        messagingTemplate.convertAndSend(destination, result);
    }
    @MessageMapping("/game.makeMoveToBot")
    public void moveOnBot(@Payload MoveInputDTO move) {
        //System.out.println(userDetails.getUsername());
        System.out.println(move.move);
        System.out.println(move.roomId);

        MoveResultDTO result=gameService.makeAMoveToBot(move);
        String destination = "/room/game." + move.roomId;
        messagingTemplate.convertAndSend(destination, result);
    }
    @MessageMapping("/game.makeMoveToPlayer")

    public void moveOnPlayer(@Payload MoveInputDTO move){
        System.out.println(move.move);
        System.out.println(move.roomId);

        MoveResultDTO result=gameService.makeAMoveToPlayer(move);

        String destination = "/room/game." + move.roomId;
        messagingTemplate.convertAndSend(destination, result);
    }
}
