package com.sorokaandriy.delivery_service.dto;

import com.sorokaandriy.delivery_service.entity.DeliveryStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record DeliveryResponse(
        UUID id,
        UUID orderId,
        UUID riderId,
        DeliveryStatus status,
        Instant createdAt,
        Instant assignedAt,
        Instant completedAt
) {
}
