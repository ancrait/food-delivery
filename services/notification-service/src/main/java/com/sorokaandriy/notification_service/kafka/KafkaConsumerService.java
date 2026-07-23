package com.sorokaandriy.notification_service.kafka;

import com.sorokaandriy.notification_service.dto.UserRegisteredEvent;
import com.sorokaandriy.notification_service.entity.NotificationStatus;
import com.sorokaandriy.notification_service.service.EmailService;
import com.sorokaandriy.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final EmailService emailService;
    private final NotificationService notificationService;

    @KafkaListener(topics = "${kafka.topics.user-registered}", groupId = "${spring.kafka.consumer.group-id}")
    public void getUserRegisterEvent(UserRegisteredEvent event) {
        log.info("Received user.registered event for userId={}", event.userId());

        boolean sent = emailService.sendVerificationEmail(event.email(), event.firstName(), event.verificationToken());

        String subject = "Verify your email";
        String body = String.format("Verification link: http://localhost:3000/verify?token=%s",
                event.verificationToken());

        if (sent) {
            notificationService.createEmailNotification(event, subject, body, NotificationStatus.SENT, null);
        } else {
            notificationService.createEmailNotification(event, subject, body, NotificationStatus.FAILED,
                    "Failed to send email via SMTP");
        }
    }
}
