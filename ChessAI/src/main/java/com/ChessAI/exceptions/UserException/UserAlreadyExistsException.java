package com.ChessAI.exceptions.UserException;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = org.springframework.http.HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends RuntimeException {
    private static final String message = "User already exists";

    public UserAlreadyExistsException(String message) {
        super(message);
    }
    public UserAlreadyExistsException() {
        super(message);
    }
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
    public UserAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
