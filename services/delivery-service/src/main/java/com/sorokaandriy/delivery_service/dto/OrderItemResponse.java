package com.sorokaandriy.delivery_service.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderItemResponse(
        UUID id,
        UUID menuItemId,
        String name,
        BigDecimal price,
        Integer quantity
) {
}
