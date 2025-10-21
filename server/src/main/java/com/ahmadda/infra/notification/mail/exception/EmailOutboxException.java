package com.ahmadda.infra.notification.mail.exception;

public class EmailOutboxException extends RuntimeException {

    public EmailOutboxException(final String message) {
        super(message);
    }
}
