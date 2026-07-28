package com.sorokaandriy.restaurant_service.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record RestaurantResponse(
        UUID id,
        String name,
        String description,
        String address,
        String phone,
        Double rating,
        String logoUrl,
        boolean isActive,
        List<MenuItemResponse> menuItems
) {}

