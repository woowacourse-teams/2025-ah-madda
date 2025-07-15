package com.ahmadda.application.exception;

public class NotFoundException extends BusinessFlowViolatedException {

    public NotFoundException(final String message) {
        super(message);
    }
}
