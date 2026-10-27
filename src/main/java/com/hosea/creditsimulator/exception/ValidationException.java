package com.hosea.creditsimulator.exception;

/**
 * Thrown when a user-supplied value breaks one of the loan rules.
 * The message is meant to be shown to the user as-is.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
