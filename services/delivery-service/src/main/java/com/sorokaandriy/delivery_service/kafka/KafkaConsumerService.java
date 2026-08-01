package com.sorokaandriy.delivery_service.kafka;

import com.sorokaandriy.delivery_service.dto.OrderCancelledEvent;
import com.sorokaandriy.delivery_service.dto.OrderCreatedEvent;
import com.sorokaandriy.delivery_service.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final DeliveryService deliveryService;


    @KafkaListener(topics = "${kafka.topics.order-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void getOrderCreated(OrderCreatedEvent event){
        log.info("Received order.created event for userId={}", event.id());
        deliveryService.assignDelivery(event);

    }


    @KafkaListener(topics = "${kafka.topics.order-cancel}", groupId = "${spring.kafka.consumer.group-id}")
    public void getOrderCancelled(OrderCancelledEvent event) {
        log.info("Received order.cancelled event for orderId={}", event.orderId());
        deliveryService.cancelDelivery(event.orderId());
    }
}
