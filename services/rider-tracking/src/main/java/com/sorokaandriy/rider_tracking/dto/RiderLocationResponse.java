package com.sorokaandriy.rider_tracking.dto;

import com.sorokaandriy.rider_tracking.entity.RiderStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record RiderLocationResponse(

        UUID id,
        UUID riderId,
        RiderStatus status,
        Double latitude,
        Double longitude,
        Double distanceKm,
        Instant updatedAt

) {
}
