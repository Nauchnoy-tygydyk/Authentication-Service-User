package org.example.authenticationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<String> handleInvalidCredentials(BadCredentialsException e) {
    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(e.getMessage());
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<String> handleUserAlreadyExists(UserAlreadyExistsException e) {
    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(e.getMessage());
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(e.getMessage());
  }
}