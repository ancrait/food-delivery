package com.sorokaandriy.delivery_service.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeliveryAcceptedEvent(
        UUID deliveryId,
        UUID orderId,
        UUID riderId
) {
}
