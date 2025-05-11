package com.ChessAI.exceptions.InvalidActionException;

public class GameEndedException extends RuntimeException{

    protected static final String message = "Invalid action, Game ended";
    public GameEndedException() {
        super(message);
    }

    public GameEndedException(String message) {
        super(message);
    }

    public GameEndedException(Throwable cause) {
        super(message, cause);
    }

    public GameEndedException(String message, Throwable cause) {
        super(message, cause);
    }
}
