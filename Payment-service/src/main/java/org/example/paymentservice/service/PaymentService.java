package org.example.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.example.paymentservice.client.IRandomClientNumber;
import org.example.paymentservice.dto.PaymentRequestDto;
import org.example.paymentservice.dto.PaymentResponseDto;
import org.example.paymentservice.event.PaymentEvent;
import org.example.paymentservice.producer.PaymentProducer;
import org.example.paymentservice.entity.Payment;
import org.example.paymentservice.entity.PaymentStatus;
import org.example.paymentservice.mapper.IPaymentMapper;
import org.example.paymentservice.repository.IPaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final IRandomClientNumber iRandomClientNumber;
  private final IPaymentMapper iPaymentMapper;
  private  final IPaymentRepository iPaymentRepository;
  private final PaymentProducer paymentProducer;

  private BigDecimal CountAllPayments(List<Payment> payments){
   return payments.stream().filter(p ->p.getStatus() == PaymentStatus.SUCCESS).map(Payment::getPaymentAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public BigDecimal getTotalSumForUser(Long userId, LocalDateTime start, LocalDateTime end) {
    List<Payment> allPaymentsInRange = iPaymentRepository.findByTimestampBetween(start, end);

    List<Payment> userPayments = allPaymentsInRange.stream()
            .filter(p -> p.getUserId().equals(userId))
            .toList();

    return CountAllPayments(userPayments);
  }

  public BigDecimal getTotalSumForAll(LocalDateTime start, LocalDateTime end) {
    return CountAllPayments(iPaymentRepository.findByTimestampBetween(start, end));
  }

  public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto){
   Payment payment = iPaymentMapper.toEntity(paymentRequestDto);

   Integer number = iRandomClientNumber.getPrediction();
    if (number % 2 == 0) {
      payment.setStatus(PaymentStatus.SUCCESS);
    }
    else {
      payment.setStatus(PaymentStatus.FAILED);
    }
    payment.setTimestamp(LocalDateTime.now());
    Payment saved = iPaymentRepository.save(payment);
    PaymentEvent paymentEvent = new PaymentEvent(saved.getOrderId(), saved.getStatus().name());
    paymentProducer.sendPaymentEvent(paymentEvent);
      return iPaymentMapper.toDto(saved);
    }


  public List<Payment> getPaymentByUserId(Long userId){
    return iPaymentRepository.findByUserId(userId);
  }

  public List<Payment> getPaymentByOrderId(Long orderId){
    return iPaymentRepository.findByOrderId(orderId);
  }

  public List<Payment> getPaymentByStatus(PaymentStatus status){
    return iPaymentRepository.findByStatus(status);
  }

}
