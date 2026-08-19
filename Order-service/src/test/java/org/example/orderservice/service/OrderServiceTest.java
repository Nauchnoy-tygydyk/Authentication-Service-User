package org.example.orderservice.service;

import org.example.orderservice.Entity.Item;
import org.example.orderservice.Entity.Order;
import org.example.orderservice.Entity.OrderStatus;
import org.example.orderservice.Repository.IItemRepository;
import org.example.orderservice.Repository.IOrderRepository;
import org.example.orderservice.client.IUserClient;
import org.example.orderservice.dto.*;
import org.example.orderservice.exception.ResourceNotFoundException;
import org.example.orderservice.mapper.IOrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

  @Mock private IOrderRepository orderRepository;
  @Mock private IItemRepository itemRepository;
  @Mock private IUserClient userClient;
  @Mock private IOrderMapper orderMapper;

  @InjectMocks private OrderService orderService;

  private Order order;
  private UserDto userDto;
  private Item testItem;

  @BeforeEach
  void setUp() {
    order = new Order();
    order.setId(1L);
    order.setUserEmail("test@mail.com");
    order.setItems(new ArrayList<>());

    userDto = new UserDto(1L, "Ivan", "Ivanov", null, "test@mail.com", true);

    testItem = new Item();
    testItem.setId(1L);
    testItem.setName("Test Product");
    testItem.setPrice(BigDecimal.valueOf(100.0));
  }

  @Test
  @DisplayName("Создание заказа - Успех")
  void createOrder_Success() {
    OrderItemDto itemDto = new OrderItemDto(1L, 2);
    OrderRequestDto request = new OrderRequestDto(List.of(itemDto), "test@mail.com");

    when(userClient.getUserByEmail(anyString())).thenReturn(userDto);
    when(orderMapper.toEntity(request)).thenReturn(order);
    when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
    when(orderRepository.save(any())).thenReturn(order);
    when(orderMapper.toDto(any(), any())).thenReturn(new OrderResponseDto());

    OrderResponseDto result = orderService.createOrder(request);

    assertNotNull(result);
    verify(orderRepository).save(any(Order.class));
    assertEquals(1L, order.getUserId());
  }

  @Test
  @DisplayName("Поиск заказа по ID - Успех")
  void findOrderById_Success() {
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(userClient.getUserByEmail(any())).thenReturn(userDto);
    when(orderMapper.toDto(any(), any())).thenReturn(new OrderResponseDto());

    OrderResponseDto result = orderService.findOrderById(1L);

    assertNotNull(result);
    verify(orderRepository).findById(1L);
  }

  @Test
  @DisplayName("Поиск заказа по ID - Ошибка (Не найден)")
  void findOrderById_NotFound() {
    when(orderRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> orderService.findOrderById(1L));
  }

  @Test
  @DisplayName("Обновление заказа - Успех")
  void updateOrder_Success() {
    OrderItemDto itemDto = new OrderItemDto(1L, 5);
    OrderRequestDto updateDto = new OrderRequestDto(List.of(itemDto), "test@mail.com");

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
    when(orderRepository.save(any())).thenReturn(order);
    when(userClient.getUserByEmail(any())).thenReturn(userDto);
    when(orderMapper.toDto(any(), any())).thenReturn(new OrderResponseDto());

    OrderResponseDto result = orderService.updateOrder(1L, updateDto);

    assertNotNull(result);
    verify(orderRepository).save(order);
  }

  @Test
  @DisplayName("Удаление заказа - Успех")
  void deleteOrder_Success() {
    when(orderRepository.existsById(1L)).thenReturn(true);

    orderService.deleteOrder(1L);

    verify(orderRepository).deleteById(1L);
  }

  @Test
  @DisplayName("Удаление заказа - Ошибка (Не существует)")
  void deleteOrder_NotFound() {
    when(orderRepository.existsById(1L)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> orderService.deleteOrder(1L));
  }

  @Test
  @DisplayName("Получение заказов с фильтрами - Успех")
  void getOrderWithFilters_Success() {
    Page<Order> page = new PageImpl<>(List.of(order));
    when(orderRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);
    when(userClient.getUserByEmail(anyString())).thenReturn(userDto);

    var result = orderService.getOrderWithFilters(OrderStatus.NEW, null, null, PageRequest.of(0, 10));

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
  }
}