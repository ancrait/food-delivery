package com.sorokaandriy.payment_service.kafka;


import com.sorokaandriy.payment_service.dto.PaymentCancelEvent;
import com.sorokaandriy.payment_service.dto.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.payment-success}")
    private String paymentPayTopic;

    @Value("${kafka.topics.payment-cancel}")
    private String paymentCancelTopic;


    public void sendPaymentSuccess(PaymentSuccessEvent event){
        kafkaTemplate.send(paymentPayTopic, event.paymentId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send payment.success event for paymentId={}: {}", event.paymentId(), ex.getMessage());
                    } else {
                        log.debug("Sent payment.success event for paymentId={}, offset={}", event.paymentId(),
                                result.getRecordMetadata().offset());
                    }
                });
    }


    public void sendPaymentCancel(PaymentCancelEvent event){
        kafkaTemplate.send(paymentCancelTopic, event.paymentId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send payment.cancel event for paymentId={}: {}", event.paymentId(), ex.getMessage());
                    } else {
                        log.debug("Sent payment.cancel event for paymentId={}, offset={}", event.paymentId(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
