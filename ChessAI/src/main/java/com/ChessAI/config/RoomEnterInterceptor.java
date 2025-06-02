package com.ChessAI.config;

import com.ChessAI.services.ApplicationUserDetailsService;
import com.ChessAI.services.GameService;
import com.ChessAI.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.ChessAI.exceptions.JWTException.JWTTokenException;
import com.ChessAI.exceptions.HTTPException.HTTPHeaderException;
import org.slf4j.Logger;

@Component
public class RoomEnterInterceptor implements ChannelInterceptor {
    @Autowired
    private JWTService jwtService;

    @Autowired
    private GameService gameService;

    @Autowired
    private ApplicationUserDetailsService userDetailsService;

    @Autowired
    private Logger logger;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        //StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            System.out.println("DISCONNECT");
            return message;
        }

        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new JWTTokenException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUserName(token);
        //System.out.println(username);
        if (username == null) {
            throw new JWTTokenException("Invalid JWT token: No username");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!jwtService.validateToken(token, userDetails)) {
            throw new JWTTokenException("Invalid JWT token");
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);
        accessor.setUser(authToken);
        logger.debug("Authenticated user: {}", username);

        if (StompCommand.UNSUBSCRIBE.equals(accessor.getCommand())) {
            System.out.println("UNSUBSCRIBE");
            gameService.leaveGame(username);
        }

        return message;
    }
    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        // Clear the security context after the message is processed
        SecurityContextHolder.clearContext();
    }
}
