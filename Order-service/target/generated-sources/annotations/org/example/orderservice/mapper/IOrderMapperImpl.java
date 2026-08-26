package org.example.orderservice.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.example.orderservice.Entity.Item;
import org.example.orderservice.Entity.Order;
import org.example.orderservice.Entity.OrderItem;
import org.example.orderservice.dto.OrderItemDto;
import org.example.orderservice.dto.OrderRequestDto;
import org.example.orderservice.dto.OrderResponseDto;
import org.example.orderservice.dto.UserDto;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-24T12:25:07+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.11 (Microsoft)"
)
@Component
public class IOrderMapperImpl implements IOrderMapper {

    @Override
    public Order toEntity(OrderRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Order order = new Order();

        order.setUserEmail( dto.getUserEmail() );
        order.setItems( orderItemDtoListToOrderItemList( dto.getItems() ) );

        return order;
    }

    @Override
    public OrderItem toEntity(OrderItemDto dto) {
        if ( dto == null ) {
            return null;
        }

        OrderItem orderItem = new OrderItem();

        orderItem.setItem( orderItemDtoToItem( dto ) );
        orderItem.setQuantity( dto.getQuantity() );

        return orderItem;
    }

    @Override
    public OrderResponseDto toDto(Order order, UserDto userDto) {
        if ( order == null && userDto == null ) {
            return null;
        }

        OrderResponseDto orderResponseDto = new OrderResponseDto();

        if ( order != null ) {
            orderResponseDto.setId( order.getId() );
            orderResponseDto.setStatus( order.getStatus() );
            orderResponseDto.setTotalPrice( order.getTotalPrice() );
            orderResponseDto.setCreatedAt( order.getCreatedAt() );
            orderResponseDto.setItems( orderItemListToOrderItemDtoList( order.getItems() ) );
        }
        orderResponseDto.setUser( userDto );

        return orderResponseDto;
    }

    @Override
    public OrderItemDto toDto(OrderItem entity) {
        if ( entity == null ) {
            return null;
        }

        OrderItemDto orderItemDto = new OrderItemDto();

        orderItemDto.setItemId( entityItemId( entity ) );
        orderItemDto.setQuantity( entity.getQuantity() );

        return orderItemDto;
    }

    protected List<OrderItem> orderItemDtoListToOrderItemList(List<OrderItemDto> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItem> list1 = new ArrayList<OrderItem>( list.size() );
        for ( OrderItemDto orderItemDto : list ) {
            list1.add( toEntity( orderItemDto ) );
        }

        return list1;
    }

    protected Item orderItemDtoToItem(OrderItemDto orderItemDto) {
        if ( orderItemDto == null ) {
            return null;
        }

        Item item = new Item();

        item.setId( orderItemDto.getItemId() );

        return item;
    }

    protected List<OrderItemDto> orderItemListToOrderItemDtoList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItemDto> list1 = new ArrayList<OrderItemDto>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( toDto( orderItem ) );
        }

        return list1;
    }

    private Long entityItemId(OrderItem orderItem) {
        Item item = orderItem.getItem();
        if ( item == null ) {
            return null;
        }
        return item.getId();
    }
}
