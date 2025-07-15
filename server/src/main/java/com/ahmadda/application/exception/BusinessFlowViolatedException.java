package com.ahmadda.application.exception;

public class BusinessFlowViolatedException extends RuntimeException {

    public BusinessFlowViolatedException(final String message) {
        super(message);
    }
}
