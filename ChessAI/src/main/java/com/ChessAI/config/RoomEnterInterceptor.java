package com.ChessAI.config;

import com.ChessAI.services.ApplicationUserDetailsService;
import com.ChessAI.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
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
    private ApplicationUserDetailsService userDetailsService;

    @Autowired
    private Logger logger;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                logger.debug("Auth token: {}", token);
                String username = jwtService.extractUserName(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtService.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        accessor.setUser(authToken);
                        logger.debug("RoomEnterInterceptor: User authenticated: {}", username);
                    } else {
                        throw new JWTTokenException();
                    }
                } else{
                    throw new JWTTokenException("JWT token is missing or invalid");
                }
            } else {
                throw new HTTPHeaderException("Authorization header is missing");
            }
        }
        else {
            logger.debug("preSend(): STOMP command is not CONNECT: {}", accessor.getCommand());
        }

        return message;
    }
}
