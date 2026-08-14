package com.sorokaandriy.rider_tracking.service.mapper;

import com.sorokaandriy.rider_tracking.dto.RiderLocationRequest;
import com.sorokaandriy.rider_tracking.dto.RiderLocationResponse;
import com.sorokaandriy.rider_tracking.entity.RiderLocation;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RiderLocationMapper {



    public RiderLocationResponse fromRiderLocationToRiderLocationResponse(RiderLocation riderLocation) {

        return RiderLocationResponse.builder()
                .id(riderLocation.getId())
                .riderId(riderLocation.getRiderId())
                .status(riderLocation.getStatus())
                .latitude(riderLocation.getLatitude())
                .longitude(riderLocation.getLongitude())
                .updatedAt(riderLocation.getUpdatedAt())
                .build();
    }
}
