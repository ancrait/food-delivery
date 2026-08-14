package com.sorokaandriy.order_service.kafka;

import com.sorokaandriy.order_service.dto.PaymentCancelEvent;
import com.sorokaandriy.order_service.dto.PaymentSuccessEvent;
import com.sorokaandriy.order_service.entity.Status;
import com.sorokaandriy.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaOrderConsumer {

    private final OrderService service;


    @KafkaListener(topics = "${kafka.topics.payment-success}", groupId = "${spring.kafka.consumer.group-id}")
    public void getPaymentSuccess(PaymentSuccessEvent event){
        log.info("Received payment.success event for orderId={}", event.orderId());
        service.updateOrderStatus(event.orderId(), Status.CONFIRMED);
    }




}
