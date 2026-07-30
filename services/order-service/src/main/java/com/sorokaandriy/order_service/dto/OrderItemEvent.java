package com.sorokaandriy.order_service.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderItemEvent(
        UUID menuItemId,
        String name,
        BigDecimal price,
        Integer quantity
) {
}
