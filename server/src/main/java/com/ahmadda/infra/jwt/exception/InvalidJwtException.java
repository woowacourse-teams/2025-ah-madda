package com.ahmadda.infra.jwt.exception;

public class InvalidJwtException extends RuntimeException {

    public InvalidJwtException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
