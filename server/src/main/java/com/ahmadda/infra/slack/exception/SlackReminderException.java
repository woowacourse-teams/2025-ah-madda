package com.ahmadda.infra.slack.exception;

public class SlackReminderException extends RuntimeException {

    public SlackReminderException(String message) {
        super(message);
    }
}
