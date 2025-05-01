package com.ChessAI.exceptions;

import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = org.springframework.http.HttpStatus.UNAUTHORIZED)
public class AuthenticationFailedException extends AuthenticationException {
    private static final String message = "Authentication failed";

    public AuthenticationFailedException() {
        super(message);
    }

    public AuthenticationFailedException(String message) {
        super(message);
    }

    public AuthenticationFailedException(Throwable cause) {
        super(message, cause);
    }

    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
