package com.ChessAI.exceptions.InvalidActionException;

public class UnauthorizedGameAccessException extends RuntimeException{
    protected static final String message = "You dont have access to this game";
    public UnauthorizedGameAccessException() {
        super(message);
    }

    public UnauthorizedGameAccessException(String message) {
        super(message);
    }

    public UnauthorizedGameAccessException(Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedGameAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
