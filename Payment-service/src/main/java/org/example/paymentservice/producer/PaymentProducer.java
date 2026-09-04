package org.example.paymentservice.producer;

import lombok.RequiredArgsConstructor;
import org.example.paymentservice.event.PaymentEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentProducer {

  private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

  public void sendPaymentEvent(PaymentEvent paymentEvent){

    kafkaTemplate.send("payment-topic", paymentEvent);
  }
}
