package com.sorokaandriy.delivery_service.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeliveryCompletedEvent(
    UUID deliveryId,
    UUID orderId,
    UUID riderId
) {}
