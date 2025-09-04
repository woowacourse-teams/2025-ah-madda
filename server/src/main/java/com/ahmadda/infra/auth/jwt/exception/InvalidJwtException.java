package com.ahmadda.infra.auth.jwt.exception;

public class InvalidJwtException extends RuntimeException {

    public InvalidJwtException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
