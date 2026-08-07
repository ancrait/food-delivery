package com.sorokaandriy.payment_service.dto;

import java.time.Instant;

public record ErrorResponseDto(
        String message,
        Instant errorTime
) {
}
