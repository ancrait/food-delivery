package com.sorokaandriy.restaurant_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RestaurantRequest(

        @NotBlank @Size(max = 256)
        String name,
        @NotBlank
        String description,
        @NotBlank @Size(max = 256)
        String address,
        @NotBlank @Pattern(regexp = "^\\+?[0-9]{10,15}$")
        String phone,
        @NotNull
        Double rating,
        @Size(max = 256)
        String logoUrl,
        boolean isActive,
        @NotNull
        Double latitude,
        @NotNull
        Double longitude

) {
}
