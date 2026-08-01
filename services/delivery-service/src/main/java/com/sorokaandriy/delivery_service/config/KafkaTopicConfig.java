package com.sorokaandriy.delivery_service.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topics.delivery-accepted}")
    private String deliveryAccepted;

    @Value("${kafka.topics.delivery-completed}")
    private String deliveryCompleted;


    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name(deliveryAccepted)
                .partitions(3)
                .replicas(1)
                .build();
    }


    @Bean
    public NewTopic deliveryCompletedTopic() {
        return TopicBuilder.name(deliveryCompleted)
                .partitions(3)
                .replicas(1)
                .build();
    }


}
