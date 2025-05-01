package com.ChessAI.exceptions.HTTPException;

public class HTTPHeaderException extends BaseHTTPException {
    private static final String message = "Invalid HTTP header";

    public HTTPHeaderException() {
        super(message);
    }

    public HTTPHeaderException(String message) {
        super(message);
    }

    public HTTPHeaderException(Throwable cause) {
        super(message, cause);
    }

    public HTTPHeaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
