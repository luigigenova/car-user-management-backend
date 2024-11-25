package com.desafio.exception;

/**
 * Exception thrown when login or password is invalid.
 */
public class InvalidLoginException extends RuntimeException {
    public InvalidLoginException(String message) {
        super(message);
    }
}
