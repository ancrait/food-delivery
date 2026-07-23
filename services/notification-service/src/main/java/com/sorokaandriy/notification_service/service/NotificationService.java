package com.sorokaandriy.notification_service.service;

import com.sorokaandriy.notification_service.dto.UserRegisteredEvent;
import com.sorokaandriy.notification_service.entity.Notification;
import com.sorokaandriy.notification_service.entity.NotificationStatus;
import com.sorokaandriy.notification_service.entity.NotificationType;
import com.sorokaandriy.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;


    public void createEmailNotification(UserRegisteredEvent event, String subject, String text,
                                         NotificationStatus status, String errorMessage) {
        Notification notification =
                Notification.builder()
                        .userId(event.userId())
                        .type(NotificationType.EMAIL_VERIFICATION)
                        .recipient(event.email())
                        .subject(subject)
                        .body(text)
                        .status(status)
                        .errorMessage(errorMessage)
                        .sendAt(status == NotificationStatus.SENT ? Instant.now() : null)
                        .createdAt(Instant.now())
                        .build();
        notificationRepository.save(notification);
    }
}
