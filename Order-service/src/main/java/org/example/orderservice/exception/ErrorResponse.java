package org.example.orderservice.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
  LocalDateTime time,
  String info
  ){}

