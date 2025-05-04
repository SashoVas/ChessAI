package com.ChessAI.exceptions.UserException;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = org.springframework.http.HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    private static final String message = "User not found";

    public UserNotFoundException(String message) {
        super(message);
    }
    public UserNotFoundException() {
        super(message);
    }
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public UserNotFoundException(Throwable cause) {
        super(cause);
    }
}
