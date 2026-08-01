package com.sorokaandriy.delivery_service.kafka;

import com.sorokaandriy.delivery_service.dto.DeliveryAcceptedEvent;
import com.sorokaandriy.delivery_service.dto.DeliveryCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.delivery-accepted}")
    private String deliveryAcceptedTopic;

    @Value("${kafka.topics.delivery-completed}")
    private String deliveryCompleted;

    public void sendDeliveryAccepted(DeliveryAcceptedEvent event){
        kafkaTemplate.send(deliveryAcceptedTopic, event.deliveryId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send delivery.accepted event for deliveryId={}: {}", event.deliveryId(), ex.getMessage());
                    } else {
                        log.debug("Sent delivery.accepted event for deliveryId={}, offset={}", event.deliveryId(),
                                result.getRecordMetadata().offset());
                    }
                });
    }


    public void sendDeliveryCompleted(DeliveryCompletedEvent event){
        kafkaTemplate.send(deliveryCompleted, event.deliveryId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send delivery.completed event for deliveryId={}: {}", event.deliveryId(), ex.getMessage());
                    } else {
                        log.debug("Sent delivery.completed event for deliveryId={}, offset={}", event.deliveryId(),
                                result.getRecordMetadata().offset());
                    }
                });
    }

}
