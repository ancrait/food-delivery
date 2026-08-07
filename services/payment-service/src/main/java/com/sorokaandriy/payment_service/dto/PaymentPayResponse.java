package com.sorokaandriy.payment_service.dto;

import lombok.Builder;

@Builder
public record PaymentPayResponse(
        String clientSecret,
        PaymentResponse payment
) {}
