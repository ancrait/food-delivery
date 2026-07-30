package com.sorokaandriy.order_service.dto;

import com.sorokaandriy.order_service.entity.Order;
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
