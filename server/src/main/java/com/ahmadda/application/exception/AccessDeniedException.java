package com.ahmadda.application.exception;

public class AccessDeniedException extends BusinessFlowViolatedException {

    public AccessDeniedException(final String message) {
        super(message);
    }
}
