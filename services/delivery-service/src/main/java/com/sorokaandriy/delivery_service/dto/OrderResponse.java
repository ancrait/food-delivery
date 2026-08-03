package com.sorokaandriy.delivery_service.dto;


import lombok.Builder;
import com.sorokaandriy.delivery_service.entity.Status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderResponse(
        UUID id,
        UUID userId,
        UUID restaurantId,
        Status status,
        BigDecimal totalPrice,
        String deliveryAddress,
        String phone,
        String notes,
        Instant createdAt,
        List<OrderItemResponse> orderItems
) {
}
