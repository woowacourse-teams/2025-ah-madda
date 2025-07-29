package com.ahmadda.domain.exception;

public class BusinessRuleViolatedException extends RuntimeException {

    public BusinessRuleViolatedException(final String message) {
        super(message);
    }
}
