package com.ahmadda.infra.notification.slack.exception;

public class SlackAlarmException extends RuntimeException {

    public SlackAlarmException(final String message) {
        super(message);
    }
}
