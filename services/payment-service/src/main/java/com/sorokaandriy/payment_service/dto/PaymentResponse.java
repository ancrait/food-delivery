package com.sorokaandriy.payment_service.dto;

import com.sorokaandriy.payment_service.entity.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record PaymentResponse(
        UUID id,
        UUID orderId,
        UUID userId,
        BigDecimal amount,
        String currency,
        PaymentStatus paymentStatus,
        String stripePaymentId,
        Instant createdAt
) {
}
