package com.ahmadda.infra.image.exception;

public class AwsImageUploadException extends RuntimeException {

    public AwsImageUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
