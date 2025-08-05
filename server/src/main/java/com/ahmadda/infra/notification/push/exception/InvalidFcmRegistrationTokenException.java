package com.ahmadda.infra.notification.push.exception;

public class InvalidFcmRegistrationTokenException extends RuntimeException {

    public InvalidFcmRegistrationTokenException(final String message) {
        super(message);
    }
}
