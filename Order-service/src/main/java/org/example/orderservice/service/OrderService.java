package org.example.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.Entity.Item;
import org.example.orderservice.Entity.Order;
import org.example.orderservice.Entity.OrderItem;
import org.example.orderservice.Entity.OrderStatus;
import org.example.orderservice.Repository.IItemRepository;
import org.example.orderservice.Repository.IOrderRepository;
import org.example.orderservice.client.IUserClient;
import org.example.orderservice.dto.OrderRequestDto;
import org.example.orderservice.dto.OrderResponseDto;
import org.example.orderservice.dto.UserDto;
import org.example.orderservice.exception.ResourceNotFoundException;
import org.example.orderservice.mapper.IOrderMapper;
import org.example.orderservice.specification.OrderSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final IOrderRepository iOrderRepository;
  private final IItemRepository iItemRepository;
  private final IUserClient iUserClient;
  private final IOrderMapper iOrderMapper;

  @Transactional
  public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
    UserDto userDto = iUserClient.getUserByEmail(orderRequestDto.getUserEmail());

    if (userDto.getId() == null) {
      throw new IllegalStateException("User Service returned DTO without ID. Check UserDto contract.");
    }

    Order order = iOrderMapper.toEntity(orderRequestDto);
    order.setUserId(userDto.getId());
    order.setStatus(OrderStatus.NEW);

    BigDecimal totalPrice = BigDecimal.ZERO;
    List<OrderItem> orderItems = new ArrayList<>();

    for (var itemDto : orderRequestDto.getItems()) {
      Item item = iItemRepository.findById(itemDto.getItemId())
              .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemDto.getItemId()));

      OrderItem orderItem = new OrderItem();
      orderItem.setItem(item);
      orderItem.setQuantity(itemDto.getQuantity());
      orderItem.setOrder(order);

      orderItems.add(orderItem);

      BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
      totalPrice = totalPrice.add(itemTotal);
    }

    order.setItems(orderItems);
    order.setTotalPrice(totalPrice);

    Order savedOrder = iOrderRepository.save(order);

    return iOrderMapper.toDto(savedOrder, userDto);
  }

  public OrderResponseDto findOrderById(Long orderId) {
    Order order = iOrderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

    UserDto userDto = iUserClient.getUserByEmail(order.getUserEmail());
    return iOrderMapper.toDto(order, userDto);
  }

  public List<OrderResponseDto> findOrderByUserId(Long userId) {
    List<Order> orders = iOrderRepository.findByUserId(userId);
    return orders.stream()
            .map(order -> {
              UserDto userDto = iUserClient.getUserByEmail(order.getUserEmail());
              return iOrderMapper.toDto(order, userDto);
            })
            .toList();
  }

  @Transactional
  public OrderResponseDto updateOrder(Long orderId, OrderRequestDto updateDto) {
    Order existingOrder = iOrderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

    if (updateDto.getItems() != null && !updateDto.getItems().isEmpty()) {
      existingOrder.getItems().clear();

      BigDecimal newTotalPrice = BigDecimal.ZERO;
      for (var itemDto : updateDto.getItems()) {
        Item item = iItemRepository.findById(itemDto.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setQuantity(itemDto.getQuantity());
        orderItem.setOrder(existingOrder);
        existingOrder.getItems().add(orderItem);

        newTotalPrice = newTotalPrice.add(item.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
      }
      existingOrder.setTotalPrice(newTotalPrice);
    }

    Order savedOrder = iOrderRepository.save(existingOrder);
    UserDto userDto = iUserClient.getUserByEmail(savedOrder.getUserEmail());

    return iOrderMapper.toDto(savedOrder, userDto);
  }

  @Transactional
  public void deleteOrder(Long orderId) {
    if (!iOrderRepository.existsById(orderId)) {
      throw new ResourceNotFoundException("Order not found");
    }
    iOrderRepository.deleteById(orderId);
  }

  public Page<OrderResponseDto> getOrderWithFilters(OrderStatus status, LocalDateTime start, LocalDateTime end, Pageable pageable) {
    Specification<Order> spec = Specification.where(null);

    if (status != null) {
      spec = spec.and(OrderSpecification.hasStatus(status));
    }
    if (start != null && end != null) {
      spec = spec.and(OrderSpecification.createdBetween(start, end));
    }

    return iOrderRepository.findAll(spec, pageable)
            .map(order -> {
              UserDto userDto = iUserClient.getUserByEmail(order.getUserEmail());
              return iOrderMapper.toDto(order, userDto);
            });
  }
}