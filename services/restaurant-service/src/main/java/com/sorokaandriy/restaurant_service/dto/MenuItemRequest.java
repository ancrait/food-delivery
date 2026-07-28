package com.sorokaandriy.restaurant_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MenuItemRequest(
        @NotBlank @Size(max = 256)
        String category,
        @NotBlank @Size(max = 256)
        String name,
        @NotBlank @Size(max = 256)
        String description,
        @NotNull
        BigDecimal price,
        @Size(max = 256)
        String imageUrl,
        boolean isAvailable
) {
}
