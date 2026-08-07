package com.sorokaandriy.payment_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfig {

    @Value("${kafka.topics.payment-success}")
    private String paymentPay;
    @Value("${kafka.topics.payment-cancel}")
    private String paymentCancel;

    @Bean
    public NewTopic paymentPayTopic() {
        return TopicBuilder.name(paymentPay)
                .partitions(3)
                .replicas(1)
                .build();
    }


    @Bean
    public NewTopic paymentCancelTopic() {
        return TopicBuilder.name(paymentCancel)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
