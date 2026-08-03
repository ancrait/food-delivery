package com.sorokaandriy.rider_tracking.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record RiderLocationResponse(

        UUID id,
        UUID riderId,
        Double latitude,
        Double longitude,
        Double distanceKm,
        Instant updateAt

) {
}
