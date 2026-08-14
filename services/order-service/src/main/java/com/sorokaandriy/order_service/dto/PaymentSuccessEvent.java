package com.sorokaandriy.order_service.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PaymentSuccessEvent(
        UUID orderId,
        UUID userId,
        UUID paymentId,
        BigDecimal amount
) {
}
