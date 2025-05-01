package com.ChessAI.config;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketsEventListener {

    @Autowired
    private Logger logger;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        logger.info("Client disconnected: {}", event.getSessionId());
    }
}
