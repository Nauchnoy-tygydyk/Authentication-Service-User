package org.example.paymentservice.mapper;

import org.example.paymentservice.dto.PaymentRequestDto;
import org.example.paymentservice.dto.PaymentResponseDto;
import org.example.paymentservice.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IPaymentMapper {

  Payment toEntity(PaymentRequestDto paymentRequestDto);

  PaymentResponseDto toDto(Payment payment);
}
