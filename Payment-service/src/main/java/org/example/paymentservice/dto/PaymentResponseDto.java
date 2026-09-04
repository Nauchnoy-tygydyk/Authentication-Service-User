package org.example.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.paymentservice.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {

  @NotNull
  private String id;

  @NotNull
  private Long orderId;

  @NotNull
  private Long userId;

  @NotNull
  private PaymentStatus status;

  @NotNull
  private BigDecimal paymentAmount;

  @NotNull
  private LocalDateTime timestamp;


}
