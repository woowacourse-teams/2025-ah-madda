package com.ahmadda.infra.push.exception;

public class InvalidFcmPushTokenException extends RuntimeException {

    public InvalidFcmPushTokenException(final String message) {
        super(message);
    }
}
