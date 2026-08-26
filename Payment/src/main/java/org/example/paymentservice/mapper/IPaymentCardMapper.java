package org.example.paymentservice.mapper;

import org.example.paymentservice.entity.PaymentCard;
import org.example.paymentservice.dto.PaymentCardDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IPaymentCardMapper {
  @Mapping(source = "user.id", target = "userId")
  PaymentCardDto toDto(PaymentCard card);
  @Mapping(source = "userId", target = "user.id")
  PaymentCard toEntity(PaymentCardDto paymentCardDto);
}