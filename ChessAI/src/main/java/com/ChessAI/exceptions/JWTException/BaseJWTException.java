package com.ChessAI.exceptions.JWTException;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = org.springframework.http.HttpStatus.UNAUTHORIZED)
public class BaseJWTException extends RuntimeException {
    protected static final String message = "Invalid JWT token";

    public BaseJWTException(String message) {
        super(message);
    }

    public BaseJWTException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseJWTException(Throwable cause) {
        super(cause);
    }
}
