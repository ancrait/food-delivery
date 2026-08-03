package com.sorokaandriy.rider_tracking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record RiderLocationRequest(

        @NotNull
        UUID riderId,
        @NotNull
        Double latitude,
        @NotNull
        Double longitude
) {
}
