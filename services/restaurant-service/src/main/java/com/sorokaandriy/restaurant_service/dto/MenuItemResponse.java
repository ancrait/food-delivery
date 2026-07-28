package com.sorokaandriy.restaurant_service.dto;

import lombok.Builder;

import java.util.UUID;
import java.math.BigDecimal;

@Builder
public record MenuItemResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        boolean isAvailable,
        String categoryName
) {}
