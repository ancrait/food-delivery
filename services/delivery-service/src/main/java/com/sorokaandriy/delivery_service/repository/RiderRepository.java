package com.sorokaandriy.delivery_service.repository;

import com.sorokaandriy.delivery_service.entity.Rider;
import com.sorokaandriy.delivery_service.entity.RiderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RiderRepository extends JpaRepository<Rider, UUID> {

    Optional<Rider> findByUserId(UUID userId);
    Optional<Rider> findFirstByRiderStatus(RiderStatus status);
}
