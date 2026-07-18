package com.sorokaandriy.auth_service.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserRegisteredEvent(
        UUID userId,
        String email,
        String verificationToken,
        String firstName,
        String lastName
) {}
