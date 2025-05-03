package com.ChessAI.exceptions.InvalidActionException;

public class InvalidMoveException extends RuntimeException{

    protected static final String message = "Invalid move";
    public InvalidMoveException() {
        super(message);
    }

    public InvalidMoveException(String message) {
        super(message);
    }

    public InvalidMoveException(Throwable cause) {
        super(message, cause);
    }

    public InvalidMoveException(String message, Throwable cause) {
        super(message, cause);
    }
}
