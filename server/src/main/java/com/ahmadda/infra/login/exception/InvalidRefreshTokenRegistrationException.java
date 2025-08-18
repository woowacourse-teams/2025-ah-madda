package com.ahmadda.infra.login.exception;

public class InvalidRefreshTokenRegistrationException extends RuntimeException {

    public InvalidRefreshTokenRegistrationException(final String message) {
        super(message);
    }
}
