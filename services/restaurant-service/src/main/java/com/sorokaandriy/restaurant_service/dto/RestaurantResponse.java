package com.sorokaandriy.restaurant_service.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record RestaurantResponse(
        UUID id,
        String name,
        String description,
        String address,
        String phone,
        BigDecimal rating,
        String logoUrl,
        boolean isActive,
        Double latitude,
        Double longitude,
        List<MenuItemResponse> menuItems
) {}

