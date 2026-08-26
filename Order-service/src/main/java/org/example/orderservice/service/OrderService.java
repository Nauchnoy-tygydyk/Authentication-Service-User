package org.example.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.Entity.Item;
import org.example.orderservice.Entity.Order;
import org.example.orderservice.Entity.OrderItem;
import org.example.orderservice.Entity.OrderStatus;
import org.example.orderservice.Repository.IOrderRepository;
import org.example.orderservice.Repository.IItemRepository;
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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final IOrderRepository orderRepository;
  private final IItemRepository itemRepository;
  private final IUserClient userClient;
  private final IOrderMapper orderMapper;

  @Transactional
  public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
    UserDto userDto = userClient.getUserByEmail(orderRequestDto.getUserEmail());

    if (userDto == null || userDto.getId() == -1L) {
      throw new ResourceNotFoundException("User not found with email: " + orderRequestDto.getUserEmail());
    }

    Order order = orderMapper.toEntity(orderRequestDto);
    order.setUserId(userDto.getId());
    order.setStatus(OrderStatus.NEW);

    BigDecimal totalPrice = BigDecimal.ZERO;
    List<OrderItem> orderItems = new ArrayList<>();

    for (var itemDto : orderRequestDto.getItems()) {
      Item item = itemRepository.findById(itemDto.getItemId())
              .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + itemDto.getItemId()));

      OrderItem orderItem = new OrderItem();
      orderItem.setItem(item);
      orderItem.setQuantity(itemDto.getQuantity());
      orderItem.setOrder(order);
      orderItems.add(orderItem);

      totalPrice = totalPrice.add(item.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
    }

    order.setItems(orderItems);
    order.setTotalPrice(totalPrice);

    Order savedOrder = orderRepository.save(order);
    return orderMapper.toDto(savedOrder, userDto);
  }

  public OrderResponseDto findOrderById(Long orderId) {
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

    UserDto userDto = userClient.getUserById(order.getUserId());
    return orderMapper.toDto(order, userDto);
  }

  public List<OrderResponseDto> findOrderByUserId(Long userId) {
    UserDto userDto = userClient.getUserById(userId);
    return orderRepository.findByUserId(userId).stream()
            .map(order -> orderMapper.toDto(order, userDto))
            .toList();
  }

  @Transactional
  public OrderResponseDto updateOrder(Long orderId, OrderRequestDto updateDto) {
    Order existingOrder = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

    if (updateDto.getItems() != null && !updateDto.getItems().isEmpty()) {
      existingOrder.getItems().clear();
      BigDecimal newTotalPrice = BigDecimal.ZERO;
      for (var itemDto : updateDto.getItems()) {
        Item item = itemRepository.findById(itemDto.getItemId())
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

    Order savedOrder = orderRepository.save(existingOrder);
    UserDto userDto = userClient.getUserById(savedOrder.getUserId());
    return orderMapper.toDto(savedOrder, userDto);
  }

  @Transactional
  public void deleteOrder(Long orderId) {
    if (!orderRepository.existsById(orderId)) {
      throw new ResourceNotFoundException("Order not found");
    }
    orderRepository.deleteById(orderId);
  }

  public Page<OrderResponseDto> getOrderWithFilters(OrderStatus status, LocalDateTime start, LocalDateTime end, Pageable pageable) {
    Specification<Order> spec = Specification.where(null);
    if (status != null) {
      spec = spec.and(OrderSpecification.hasStatus(status));
    }
    if (start != null && end != null) {
      spec = spec.and(OrderSpecification.createdBetween(start, end));
    }

    Page<Order> ordersPage = orderRepository.findAll(spec, pageable);
    List<Long> userIds = ordersPage.getContent().stream()
            .map(Order::getUserId)
            .distinct()
            .toList();

    Map<Long, UserDto> userMap = userClient.getUsersByIds(userIds).stream()
            .collect(Collectors.toMap(UserDto::getId, Function.identity()));

    return ordersPage.map(order -> orderMapper.toDto(order, userMap.get(order.getUserId())));
  }
}