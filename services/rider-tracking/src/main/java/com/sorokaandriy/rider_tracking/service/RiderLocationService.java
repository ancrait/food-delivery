package com.sorokaandriy.rider_tracking.service;

import com.sorokaandriy.rider_tracking.dto.RiderLocationRequest;
import com.sorokaandriy.rider_tracking.dto.RiderLocationResponse;
import com.sorokaandriy.rider_tracking.entity.RiderLocation;
import com.sorokaandriy.rider_tracking.entity.RiderStatus;
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
        validateCoordinates(request.latitude(), request.longitude());

        RiderLocation location = riderLocationRepository
                .findByRiderId(request.riderId())
                .orElse(new RiderLocation());

        location.setRiderId(request.riderId());
        location.setStatus(request.status());
        location.setLatitude(request.latitude());
        location.setLongitude(request.longitude());
        location.setUpdatedAt(Instant.now());

        riderLocationRepository.save(location);

        return mapper.fromRiderLocationToRiderLocationResponse(location);
    }

    public RiderLocationResponse updateStatus(UUID riderId, RiderStatus status) {
        RiderLocation location = riderLocationRepository.findByRiderId(riderId)
                .orElseThrow(() -> new RuntimeException("Rider location not found"));

        location.setStatus(status);
        location.setUpdatedAt(Instant.now());
        riderLocationRepository.save(location);

        return mapper.fromRiderLocationToRiderLocationResponse(location);
    }


    public RiderLocationResponse findNearestRiderLocation(Double latitude, Double longitude) {
        validateCoordinates(latitude, longitude);

        return riderLocationRepository.findAll().stream()
                .filter(riderLocation -> riderLocation.getStatus() == RiderStatus.ONLINE)
                .map(riderLocation -> Map.entry(riderLocation,
                        haversine(latitude, longitude, riderLocation.getLatitude(), riderLocation.getLongitude())))
                .min(Map.Entry.comparingByValue())
                .map(entry -> {
                    RiderLocation nearest = entry.getKey();
                    Double distance = entry.getValue();
                    return new RiderLocationResponse(
                            nearest.getId(),
                            nearest.getRiderId(),
                            nearest.getStatus(),
                            nearest.getLatitude(),
                            nearest.getLongitude(),
                            distance,
                            nearest.getUpdatedAt()
                    );
                })
                .orElseThrow(() -> new RuntimeException("No riders available"));
    }

    private void validateCoordinates(Double latitude, Double longitude) {
        if (latitude == null || latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (longitude == null || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
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
