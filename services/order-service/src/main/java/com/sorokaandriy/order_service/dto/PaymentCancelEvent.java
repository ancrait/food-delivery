package com.sorokaandriy.order_service.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentCancelEvent(
        UUID orderId,
        UUID paymentId
) {
}
