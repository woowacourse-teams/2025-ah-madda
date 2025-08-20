package com.ahmadda.infra.login.exception;

public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException(final String message) {
        super(message);
    }
}
