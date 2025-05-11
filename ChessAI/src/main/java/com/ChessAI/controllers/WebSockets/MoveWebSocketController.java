package com.ChessAI.controllers.WebSockets;

import com.ChessAI.dto.InitialConnectDTO;
import com.ChessAI.dto.MoveInputDTO;
import com.ChessAI.dto.MoveResultDTO;
import com.ChessAI.models.GameStatus;
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
        MoveResultDTO result=gameService.getCurrentGameState(input,username);
        String destination = "/room/game." + input.getRoomId();
        messagingTemplate.convertAndSend(destination, result);
    }
    @MessageMapping("/game.makeMoveToBot")
    public void moveOnBot(Principal principal,@Payload MoveInputDTO move) {
        //System.out.println(userDetails.getUsername());
        System.out.println(move.move);
        System.out.println(move.roomId);
        String username=principal.getName();
        String destination = "/room/game." + move.roomId;

        MoveResultDTO result=gameService.makeAMoveToBot(move,username);
        messagingTemplate.convertAndSend(destination, result);

        if (result.getGameState() != GameStatus.IN_PROGRESS)
            return;

        result=gameService.getBotMove(move.roomId,username);
        messagingTemplate.convertAndSend(destination, result);

    }
    @MessageMapping("/game.makeMoveToPlayer")

    public void moveOnPlayer(Principal principal,@Payload MoveInputDTO move){
        System.out.println(move.move);
        System.out.println(move.roomId);
        String username=principal.getName();

        MoveResultDTO result=gameService.makeAMoveToPlayer(move,username);

        String destination = "/room/game." + move.roomId;
        messagingTemplate.convertAndSend(destination, result);
    }
}
