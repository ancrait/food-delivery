package com.sorokaandriy.restaurant_service.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CategoryResponse(
        UUID id,
        String name
) {
}
