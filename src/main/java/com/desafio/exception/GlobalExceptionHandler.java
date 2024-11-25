package com.desafio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Captures and handles exceptions thrown by REST controllers,
 * providing standardized responses to API clients.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation exceptions for invalid request arguments.
     *
     * @param ex Exception containing validation errors.
     * @return ResponseEntity with error details.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        errors.put("message", "Validation error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handles IllegalArgumentException for missing or invalid fields.
     *
     * @param ex Exception indicating invalid arguments.
     * @return ResponseEntity with an appropriate error message.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "message", "Invalid argument provided",
                "details", ex.getMessage() != null ? ex.getMessage() : "No additional details"
        ));
    }

    /**
     * Handles exceptions for duplicate license plates.
     *
     * @param ex Exception for duplicate license plate.
     * @return ResponseEntity containing an error message for duplicate license plates.
     */
    @ExceptionHandler(DuplicateLicensePlateException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateLicensePlateException(DuplicateLicensePlateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "message", "License plate already exists"
        ));
    }

    /**
     * Handles exceptions for invalid credentials or missing authorization.
     *
     * @param ex Exception for authentication failures.
     * @return ResponseEntity containing an error message for unauthorized access.
     */
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleUnauthorizedException(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "message", "Unauthorized",
                "details", ex.getMessage()
        ));
    }

    /**
     * Handles exceptions for expired credentials.
     *
     * @param ex Exception thrown when session credentials expire.
     * @return ResponseEntity containing an error message for expired sessions.
     */
    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<Map<String, String>> handleCredentialsExpiredException(CredentialsExpiredException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "message", "Session expired",
                "details", ex.getMessage()
        ));
    }

    /**
     * Handles exceptions for duplicate email registration.
     *
     * @param ex Exception for existing email.
     * @return ResponseEntity containing an error message for duplicate email.
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "message", "Email already exists"
        ));
    }

    /**
     * Handles exceptions for duplicate login registration.
     *
     * @param ex Exception for existing login.
     * @return ResponseEntity containing an error message for duplicate login.
     */
    @ExceptionHandler(LoginAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleLoginAlreadyExistsException(LoginAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "message", "Login already exists"
        ));
    }

    /**
     * Handles exceptions for invalid login or password attempts.
     *
     * @param ex Exception thrown for invalid login or password.
     * @return ResponseEntity containing an error message for invalid credentials.
     */
    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<Map<String, String>> handleInvalidLoginException(InvalidLoginException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "message", "Invalid login or password"
        ));
    }

    /**
     * Handles generic exceptions not specifically addressed.
     *
     * @param ex General exception.
     * @return ResponseEntity containing a generic error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "message", "An unexpected error occurred",
                "details", ex.getMessage()
        ));
    }
}
