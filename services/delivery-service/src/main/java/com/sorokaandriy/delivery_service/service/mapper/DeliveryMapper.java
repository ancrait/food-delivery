package com.sorokaandriy.delivery_service.service.mapper;

import com.sorokaandriy.delivery_service.dto.*;
import com.sorokaandriy.delivery_service.entity.Delivery;
import com.sorokaandriy.delivery_service.entity.DeliveryStatus;
import com.sorokaandriy.delivery_service.entity.Rider;
import com.sorokaandriy.delivery_service.entity.RiderStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
public class DeliveryMapper {
    public Rider toRider(UUID userId) {
        return Rider.builder()
                .userId(userId)
                .riderStatus(RiderStatus.OFFLINE)
                .rating(BigDecimal.ZERO)
                .createdAt(Instant.now())
                .build();
    }

    public RiderResponse fromRiderToRiderResponse(Rider rider) {
        return RiderResponse.builder()
                .id(rider.getId())
                .userId(rider.getUserId())
                .riderStatus(rider.getRiderStatus())
                .ratting(rider.getRating())
                .build();
    }

    public DeliveryResponse fromDeliveryToDeliveryResponse(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .orderId(delivery.getOrderId())
                .riderId(delivery.getRider() != null ? delivery.getRider().getId() : null)
                .status(delivery.getDeliveryStatus())
                .createdAt(delivery.getCreatedAt())
                .assignedAt(delivery.getAssignedAt())
                .completedAt(delivery.getCompletedAt())
                .build();
    }

    public Delivery fromOrderCreatedEventToDelivery(OrderCreatedEvent event) {
        return Delivery.builder()
                .orderId(event.id())
                .deliveryStatus(DeliveryStatus.ASSIGNED)
                .build();
    }

    public DeliveryAcceptedEvent fromDeliveryToDeliveryAcceptedEvent(Delivery delivery) {

        return DeliveryAcceptedEvent.builder()
                .deliveryId(delivery.getId())
                .riderId(delivery.getRider().getId())
                .orderId(delivery.getOrderId())
                .build();
    }

    public DeliveryCompletedEvent fromDeliveryToDeliveryCompletedEvent(Delivery delivery) {

        return DeliveryCompletedEvent.builder()
                .deliveryId(delivery.getId())
                .riderId(delivery.getRider().getId())
                .orderId(delivery.getOrderId())
                .build();
    }
}
