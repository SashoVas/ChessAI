package com.ChessAI.exceptions.InvalidActionException;

public class InvalidRoomException extends RuntimeException{
    protected static final String message = "Invalid room";
    public InvalidRoomException() {
        super(message);
    }

    public InvalidRoomException(String message) {
        super(message);
    }

    public InvalidRoomException(Throwable cause) {
        super(message, cause);
    }

    public InvalidRoomException(String message, Throwable cause) {
        super(message, cause);
    }
}
