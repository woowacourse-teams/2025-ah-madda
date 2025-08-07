package com.ahmadda.domain.exception;

public class UnauthorizedOperationException extends BusinessRuleViolatedException {

    public UnauthorizedOperationException(final String message) {
        super(message);
    }
}
