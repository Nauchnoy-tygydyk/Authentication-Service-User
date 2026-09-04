package org.example.paymentservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {

  @NotNull
  @Min(1)
  private Long orderId;

  @NotNull
  @Min(1)
  private Long userId;

  @NotNull
  @Positive
  private BigDecimal paymentAmount;
}
