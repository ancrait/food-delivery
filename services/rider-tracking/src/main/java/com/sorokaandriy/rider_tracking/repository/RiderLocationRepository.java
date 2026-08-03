package com.sorokaandriy.rider_tracking.repository;

import com.sorokaandriy.rider_tracking.entity.RiderLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RiderLocationRepository extends JpaRepository<RiderLocation, UUID> {
    Optional<RiderLocation> findByRiderId(UUID riderId);
}
