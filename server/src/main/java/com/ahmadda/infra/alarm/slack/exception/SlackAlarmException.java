package com.ahmadda.infra.alarm.slack.exception;

public class SlackAlarmException extends RuntimeException {

    public SlackAlarmException(final String message) {
        super(message);
    }
}
