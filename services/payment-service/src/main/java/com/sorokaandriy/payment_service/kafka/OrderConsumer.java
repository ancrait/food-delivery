package com.sorokaandriy.payment_service.kafka;

import com.sorokaandriy.payment_service.dto.OrderCancelledEvent;
import com.sorokaandriy.payment_service.dto.OrderCreatedEvent;
import com.sorokaandriy.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "${kafka.topics.order-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void getOrderCreated(OrderCreatedEvent event){
        log.info("Received order.created event for userId={}", event.id());
        paymentService.createPayment(event);


    }

    @KafkaListener(topics = "${kafka.topics.order-cancel}", groupId = "${spring.kafka.consumer.group-id}")
    public void getOrderCanceled(OrderCancelledEvent event){
        log.info("Received order.canceled event for orderId={}", event.orderId());
        paymentService.cancelPayment(event.orderId());


    }


}
