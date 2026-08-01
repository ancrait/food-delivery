package com.sorokaandriy.delivery_service.dto;

import com.sorokaandriy.delivery_service.entity.Rider;
import com.sorokaandriy.delivery_service.entity.RiderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record RiderResponse(
        UUID id,
        UUID userId,
        RiderStatus riderStatus,
        BigDecimal ratting
) {
}
