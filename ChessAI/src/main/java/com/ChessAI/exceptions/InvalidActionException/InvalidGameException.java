package com.ChessAI.exceptions.InvalidActionException;

public class InvalidGameException extends RuntimeException{
    protected static final String message = "Invalid game";
    public InvalidGameException() {
        super(message);
    }

    public InvalidGameException(String message) {
        super(message);
    }

    public InvalidGameException(Throwable cause) {
        super(message, cause);
    }

    public InvalidGameException(String message, Throwable cause) {
        super(message, cause);
    }
}
