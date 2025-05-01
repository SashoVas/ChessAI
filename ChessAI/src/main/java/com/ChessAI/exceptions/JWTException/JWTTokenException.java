package com.ChessAI.exceptions.JWTException;

public class JWTTokenException extends BaseJWTException {
    public JWTTokenException() {
        super(message);
    }

    public JWTTokenException(String message) {
        super(message);
    }

    public JWTTokenException(Throwable cause) {
        super(message, cause);
    }

    public JWTTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
