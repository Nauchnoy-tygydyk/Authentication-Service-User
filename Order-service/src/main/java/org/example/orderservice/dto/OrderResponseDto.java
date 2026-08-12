package org.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.orderservice.Entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {

  private Long id;

  private OrderStatus status;

  private BigDecimal totalPrice;

  private LocalDateTime createdAt;

  private UserDto user;

  private List<OrderItemDto> items;
}
