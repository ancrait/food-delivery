package com.sorokaandriy.payment_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreatedPaymentRequest(
        @NotNull
        UUID orderId
) {

}
