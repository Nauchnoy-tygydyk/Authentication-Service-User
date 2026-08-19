package org.example.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {

  @NotNull
  private Long itemId;

  @NotNull
  @Min(1)
  private Integer quantity;
}
