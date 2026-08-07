package com.sorokaandriy.payment_service.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentCancelEvent(
        UUID orderId,
        UUID paymentId
) {
}
