package com.sorokaandriy.restaurant_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CategoryRequest(
        @NotBlank @Size(max = 256)
        String name

) {
}
