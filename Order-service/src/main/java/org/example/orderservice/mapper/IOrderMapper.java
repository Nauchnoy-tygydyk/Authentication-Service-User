package org.example.orderservice.mapper;

import org.example.orderservice.Entity.Order;
import org.example.orderservice.Entity.OrderItem;
import org.example.orderservice.dto.OrderItemDto;
import org.example.orderservice.dto.OrderRequestDto;
import org.example.orderservice.dto.OrderResponseDto;
import org.example.orderservice.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IOrderMapper {

  Order toEntity(OrderRequestDto dto);

  @Mapping(target = "item.id", source = "itemId")
  OrderItem toEntity(OrderItemDto dto);


  @Mapping(target = "user", source = "userDto")
  @Mapping(target = "id", source = "order.id")
  OrderResponseDto toDto(Order order, UserDto userDto);

  @Mapping(target = "itemId", source = "item.id")
  OrderItemDto toDto(OrderItem entity);
}