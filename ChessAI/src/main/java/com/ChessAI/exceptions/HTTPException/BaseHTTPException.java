package com.ChessAI.exceptions.HTTPException;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = org.springframework.http.HttpStatus.BAD_REQUEST)
public class BaseHTTPException extends RuntimeException {
    protected static final String message = "Invalid HTTP request";

    public BaseHTTPException(String message) {
        super(message);
    }

    public BaseHTTPException(Throwable cause) {
        super(message, cause);
    }

    public BaseHTTPException(String message, Throwable cause) {
        super(message, cause);
    }
}
