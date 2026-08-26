package org.example.authenticationservice.exception;

public class BadCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}