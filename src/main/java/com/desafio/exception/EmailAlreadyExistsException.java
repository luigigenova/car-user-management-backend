package com.desafio.exception;

/**
 * Exception thrown when an email is already registered.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
