package com.sorokaandriy.rider_tracking.service;

import com.sorokaandriy.rider_tracking.dto.RiderLocationRequest;
import com.sorokaandriy.rider_tracking.dto.RiderLocationResponse;
import com.sorokaandriy.rider_tracking.entity.RiderLocation;
import com.sorokaandriy.rider_tracking.exception.RiderLocationNotFoundException;
import com.sorokaandriy.rider_tracking.repository.RiderLocationRepository;
import com.sorokaandriy.rider_tracking.service.mapper.RiderLocationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RiderLocationService {

    private final RiderLocationMapper mapper;

    private final RiderLocationRepository riderLocationRepository;
    

    public RiderLocationResponse updateLocation(RiderLocationRequest request) {
        RiderLocation location = riderLocationRepository
                .findByRiderId(request.riderId())
                .orElse(new RiderLocation());

        location.setRiderId(request.riderId());
        location.setLatitude(request.latitude());
        location.setLongitude(request.longitude());
        location.setUpdatedAt(Instant.now());

        riderLocationRepository.save(location);

        return mapper.fromRiderLocationToRiderLocationResponse(location);
    }


    public RiderLocationResponse findNearestRiderLocation(Double latitude, Double longitude) {
        return riderLocationRepository.findAll().stream()
                .map(riderLocation -> Map.entry(riderLocation,
                        haversine(latitude, longitude, riderLocation.getLatitude(), riderLocation.getLongitude())))
                .min(Map.Entry.comparingByValue())
                .map(entry -> {
                    RiderLocation nearest = entry.getKey();
                    Double distance = entry.getValue();
                    return new RiderLocationResponse(
                            nearest.getId(),
                            nearest.getRiderId(),
                            nearest.getLatitude(),
                            nearest.getLongitude(),
                            distance,
                            nearest.getUpdatedAt()
                    );
                })
                .orElseThrow(() -> new RuntimeException("No riders available"));
    }



    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double r = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        return 2 * r * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }
}
