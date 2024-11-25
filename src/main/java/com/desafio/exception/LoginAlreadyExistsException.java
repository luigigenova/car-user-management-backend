package com.desafio.exception;

/**
 * Exception thrown when a login is already registered.
 */
public class LoginAlreadyExistsException extends RuntimeException {
    public LoginAlreadyExistsException(String message) {
        super(message);
    }
}
