package com.ahmadda.infra.login.exception;

public class InvalidRefreshTokenRegistrationException extends RuntimeException {

    public InvalidRefreshTokenRegistrationException(String message) {
        super(message);
    }
}
