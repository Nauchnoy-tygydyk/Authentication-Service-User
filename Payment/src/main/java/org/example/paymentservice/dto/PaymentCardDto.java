package org.example.paymentservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCardDto {

  @NotNull
  @Positive
  private Long userId;

  @NotBlank
  @Size(min = 13, max = 19)
  @Pattern(regexp = "^[0-9]+$")
  private String number;

  @NotBlank
  private String holder;

  @NotNull
  @FutureOrPresent
  private LocalDate expirationDate;

@NotNull
  private Boolean active;

}
