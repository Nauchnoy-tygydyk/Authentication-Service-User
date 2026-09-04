package org.example.paymentservice.repository;

import org.example.paymentservice.entity.Payment;
import org.example.paymentservice.entity.PaymentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public interface IPaymentRepository extends MongoRepository<Payment, String> {

   List<Payment> findByUserId(Long userId);

   List<Payment> findByOrderId(Long orderId);

   List<Payment> findByStatus(PaymentStatus status);

   List<Payment> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

}
