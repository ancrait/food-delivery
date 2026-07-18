package com.sorokaandriy.auth_service.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        Instant errorTime
) {
}
