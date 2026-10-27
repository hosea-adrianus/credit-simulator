package com.hosea.creditsimulator.exception;

/**
 * Thrown when existing loan data cannot be fetched from the web service.
 * The message is meant to be shown to the user as-is.
 */
public class LoanDataException extends RuntimeException {

    public LoanDataException(String message) {
        super(message);
    }

    public LoanDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
