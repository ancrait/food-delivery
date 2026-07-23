package com.sorokaandriy.notification_service.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    @Column(nullable = false)
    private String recipient;
    private String subject;
    @Column(nullable = false)
    private String body;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;
    @Column(name = "error_message")
    private String errorMessage;
    @Builder.Default
    @Column(name = "send_at")
    private Instant sendAt;
    @Builder.Default
    @Column(nullable = false, name = "created_at")
    private Instant createdAt = Instant.now();
}
