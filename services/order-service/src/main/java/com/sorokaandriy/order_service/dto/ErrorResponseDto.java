package com.sorokaandriy.order_service.dto;

import java.time.Instant;

public record ErrorResponseDto(
        String message,
        Instant errorTime
) {
}
