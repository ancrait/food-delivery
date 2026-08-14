package com.sorokaandriy.rider_tracking.dto;

import com.sorokaandriy.rider_tracking.entity.RiderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record RiderLocationRequest(

        @NotNull
        UUID riderId,
        @NotNull
        RiderStatus status,
        @NotNull
        Double latitude,
        @NotNull
        Double longitude
) {
}
