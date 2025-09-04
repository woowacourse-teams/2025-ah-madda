package com.ahmadda.infra.auth.oauth.exception;

public class InvalidOauthTokenException extends RuntimeException {

    public InvalidOauthTokenException(final String message) {
        super(message);
    }
}
