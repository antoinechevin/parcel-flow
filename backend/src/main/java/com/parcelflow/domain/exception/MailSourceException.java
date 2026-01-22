package com.parcelflow.domain.exception;

public class MailSourceException extends RuntimeException {
    public MailSourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
