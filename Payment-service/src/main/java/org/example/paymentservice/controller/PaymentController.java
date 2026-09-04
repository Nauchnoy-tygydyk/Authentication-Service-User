package org.example.paymentservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.paymentservice.dto.PaymentRequestDto;
import org.example.paymentservice.dto.PaymentResponseDto;
import org.example.paymentservice.entity.Payment;
import org.example.paymentservice.entity.PaymentStatus;
import org.example.paymentservice.mapper.IPaymentMapper;
import org.example.paymentservice.service.PaymentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.stream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

  private final PaymentService paymentService;

  private final IPaymentMapper iPaymentMapper;

  @PostMapping()
  public ResponseEntity<PaymentResponseDto> create(@Valid @RequestBody PaymentRequestDto paymentRequestDto, @RequestHeader("X-User-Id") Long currentUserId){
    paymentRequestDto.setUserId(currentUserId);

    PaymentResponseDto responseDto = paymentService.createPayment(paymentRequestDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

  }

  @GetMapping("/search")
  public ResponseEntity<List<PaymentResponseDto>>  find(
          @RequestParam(required = false) Long userId,

          @RequestParam(required = false) Long orderId,

          @RequestParam(required = false) PaymentStatus status
          ){

    List<Payment> payments = null;

    if(userId != null){
      payments = paymentService.getPaymentByUserId(userId);
    }
    else if(orderId != null){
      payments = paymentService.getPaymentByOrderId(orderId);
    }
    else if(status != null){
      payments = paymentService.getPaymentByStatus(status);
    }
      if (payments == null){
        payments = List.of();
      }
    List<PaymentResponseDto> response = payments.stream().map(iPaymentMapper::toDto).toList();
  return ResponseEntity.ok(response);

  }

  @GetMapping("/my-sum")
  public ResponseEntity<BigDecimal> sumOfOneUser(
          @RequestHeader("X-User-Id") Long currentUserId,
          @DateTimeFormat @RequestParam LocalDateTime start,
          @DateTimeFormat @RequestParam LocalDateTime end
          ){
    BigDecimal response = paymentService.getTotalSumForUser(currentUserId, start, end);
    return ResponseEntity.ok(response);
  }


  @GetMapping("/admin/all-sum")
  public ResponseEntity<BigDecimal> sumOfAllUsers(
          @RequestHeader("X-User-Role") String role,
          @DateTimeFormat @RequestParam LocalDateTime start,
          @DateTimeFormat @RequestParam LocalDateTime end
  ){
    if (!"ADMIN".equals(role)){
      throw new RuntimeException("Denied");
    }

    BigDecimal response = paymentService.getTotalSumForAll(start, end);
    return ResponseEntity.ok(response);
  }
}
