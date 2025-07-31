package com.ahmadda.infra.slack.exception;

public class SlackReminderException extends RuntimeException {

    public SlackReminderException(final String message) {
        super(message);
    }
}
