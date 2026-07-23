package com.sorokaandriy.notification_service.dto;

import java.util.UUID;

public record UserRegisteredEvent(
        UUID userId,
        String email,
        String verificationToken,
        String firstName,
        String lastName
) {
}
