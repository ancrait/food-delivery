package com.sorokaandriy.delivery_service.repository;

import com.sorokaandriy.delivery_service.entity.Delivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery , UUID> {
    Page<Delivery> findByRiderId(UUID riderId, Pageable pageable);

    Optional<Delivery> findByOrderId(UUID orderId);
}
