package com.sorokaandriy.auth_service.service;

import com.sorokaandriy.auth_service.dto.UserRegisteredEvent;
import com.sorokaandriy.auth_service.entity.VerificationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.user-registered}")
    private String userRegisteredTopic;

    @Value("${kafka.topics.user-verifying-email}")
    private String userVerifyTopic;

    public void sendUserRegistered(UserRegisteredEvent event) {
        kafkaTemplate.send(userRegisteredTopic, event.userId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send user.registered event for userId={}: {}", event.userId(), ex.getMessage());
                    } else {
                        log.debug("Sent user.registered event for userId={}, offset={}", event.userId(),
                                result.getRecordMetadata().offset());
                    }
                });

    }


}
