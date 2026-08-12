package org.example.orderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.orderservice.Entity.OrderStatus;
import org.example.orderservice.dto.OrderRequestDto;
import org.example.orderservice.dto.OrderResponseDto;
import org.example.orderservice.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  public ResponseEntity<OrderResponseDto> create(@Valid @RequestBody OrderRequestDto dto){
    OrderResponseDto response = orderService.createOrder(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderResponseDto> getById(@PathVariable Long id){
    OrderResponseDto response = orderService.findOrderById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<OrderResponseDto>> getUserById(@PathVariable Long userId){
    List<OrderResponseDto> response = orderService.findOrderByUserId(userId);
    return  ResponseEntity.ok(response);
  }

  @GetMapping("/search")
  public ResponseEntity<Page<OrderResponseDto>> getOrderByFilter(
          @RequestParam(required = false) OrderStatus status,
          @RequestParam(required = false) LocalDateTime start,
          @RequestParam(required = false) LocalDateTime end,
          Pageable pageable
          )
  {
    Page<OrderResponseDto> response = orderService.getOrderWithFilters(status, start, end, pageable);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<OrderResponseDto> update(@PathVariable Long id, @Valid @RequestBody OrderRequestDto dto){
      OrderResponseDto response = orderService.updateOrder(id, dto);
      return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public  ResponseEntity<Void> delete(@PathVariable Long id){
    orderService.deleteOrder(id);
    return ResponseEntity.noContent().build();
  }
}
