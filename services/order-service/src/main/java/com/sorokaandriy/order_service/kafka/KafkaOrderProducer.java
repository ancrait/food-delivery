package com.sorokaandriy.order_service.kafka;

import com.sorokaandriy.order_service.dto.OrderCancelledEvent;
import com.sorokaandriy.order_service.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaOrderProducer {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    @Value("${kafka.topics.order-created}")
    private String orderCreatedTopic;
    @Value("${kafka.topics.order-cancel}")
    private String orderCancelTopic;

    public void sendOrderCreated(OrderCreatedEvent event){
        kafkaTemplate.send(orderCreatedTopic, event.id().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send order.cerated event for order={}: {}", event.id(), ex.getMessage());
                    } else {
                        log.debug("Sent order.created event for order={}, offset={}", event.id(),
                                result.getRecordMetadata().offset());
                    }
                });
    }


    public void sendOrderCancel(OrderCancelledEvent event){
        kafkaTemplate.send(orderCancelTopic, event.orderId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send order.cancel event for order={}: {}", event.orderId(), ex.getMessage());
                    } else {
                        log.debug("Sent order.cancel event for order={}, offset={}", event.orderId(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
