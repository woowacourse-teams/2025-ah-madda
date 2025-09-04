package com.ahmadda.infra.auth.exception;

public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException(final String message) {
        super(message);
    }
}
