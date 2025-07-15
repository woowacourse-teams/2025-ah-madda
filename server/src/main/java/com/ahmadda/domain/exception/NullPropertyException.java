package com.ahmadda.domain.exception;

public class NullPropertyException extends BusinessRuleViolatedException {

    public NullPropertyException(final String message) {
        super(message);
    }
}
