package com.ChessAI.exceptions.InvalidActionException;

public class NotUserTurnException extends RuntimeException{
    protected static final String message = "Not your turn";
    public NotUserTurnException() {
        super(message);
    }

    public NotUserTurnException(String message) {
        super(message);
    }

    public NotUserTurnException(Throwable cause) {
        super(message, cause);
    }

    public NotUserTurnException(String message, Throwable cause) {
        super(message, cause);
    }
}
