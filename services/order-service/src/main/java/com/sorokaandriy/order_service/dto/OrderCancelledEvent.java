package com.sorokaandriy.order_service.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderCancelledEvent(
        UUID orderId,
        UUID userId,
        UUID restaurantId
) {
}
