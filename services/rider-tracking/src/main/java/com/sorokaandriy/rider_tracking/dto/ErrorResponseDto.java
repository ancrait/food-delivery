package com.sorokaandriy.rider_tracking.dto;

import java.time.Instant;

public record ErrorResponseDto(
        String message,
        Instant errorTime
) {
}
