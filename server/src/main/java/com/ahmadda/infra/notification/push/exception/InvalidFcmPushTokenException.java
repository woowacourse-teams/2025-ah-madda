package com.ahmadda.infra.notification.push.exception;

public class InvalidFcmPushTokenException extends RuntimeException {

    public InvalidFcmPushTokenException(final String message) {
        super(message);
    }
}
