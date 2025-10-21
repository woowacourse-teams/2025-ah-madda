package com.ahmadda.infra.image.exception;

public class AwsImageUploadException extends RuntimeException {

    public AwsImageUploadException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
