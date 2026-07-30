package org.example.paymentservice.exception;

public class LimitExceededException extends RuntimeException {
  public LimitExceededException(String message) {
    super(message);
  }
}
