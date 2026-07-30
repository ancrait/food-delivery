package com.sorokaandriy.order_service.dto;

import com.sorokaandriy.order_service.entity.Order;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderItemRequest(
        @NotNull
        UUID menuItemId,
        @NotNull @Min(1)
        Integer quantity
    )
{}
