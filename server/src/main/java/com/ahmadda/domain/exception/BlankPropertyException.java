package com.ahmadda.domain.exception;

public class BlankPropertyException extends BusinessRuleViolatedException {

    public BlankPropertyException(final String message) {
        super(message);
    }
}
