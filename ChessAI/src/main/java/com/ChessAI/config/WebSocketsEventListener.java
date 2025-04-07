package com.ChessAI.config;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketsEventListener {

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){

        System.out.println("Disconnect");

    }
}
