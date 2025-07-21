package com.ahmadda.infra.exception;

public class MailSendFailedException extends RuntimeException {

    public MailSendFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
