package com.sorokaandriy.delivery_service.service;

import com.sorokaandriy.delivery_service.Client.RestClientService;
import com.sorokaandriy.delivery_service.dto.*;
import com.sorokaandriy.delivery_service.entity.Delivery;
import com.sorokaandriy.delivery_service.entity.DeliveryStatus;
import com.sorokaandriy.delivery_service.entity.Rider;
import com.sorokaandriy.delivery_service.entity.RiderStatus;
import com.sorokaandriy.delivery_service.exception.DeliveryNotFoundException;
import com.sorokaandriy.delivery_service.exception.RiderNotFoundException;
import com.sorokaandriy.delivery_service.kafka.KafkaProducerService;
import com.sorokaandriy.delivery_service.repository.DeliveryRepository;
import com.sorokaandriy.delivery_service.repository.RiderRepository;
import com.sorokaandriy.delivery_service.service.mapper.DeliveryMapper;
import com.sorokaandriy.delivery_service.websocket.DeliveryWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class DeliveryService {

    public DeliveryService(DeliveryRepository deliveryRepository,
                           RiderRepository riderRepository,
                           DeliveryMapper mapper,
                           KafkaProducerService kafkaProducerService,
                           @Lazy DeliveryWebSocketHandler webSocketHandler,
                           RestClientService client) {
        this.deliveryRepository = deliveryRepository;
        this.riderRepository = riderRepository;
        this.mapper = mapper;
        this.kafkaProducerService = kafkaProducerService;
        this.webSocketHandler = webSocketHandler;
        this.client = client;
    }

    private final DeliveryRepository deliveryRepository;
    private final RiderRepository riderRepository;
    private final DeliveryMapper mapper;
    private final KafkaProducerService kafkaProducerService;
    private final DeliveryWebSocketHandler webSocketHandler;
    private final RestClientService client;

    public RiderResponse createRiderProfile(String token, RiderLocationRequest locationRequest) {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());

        if (riderRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("Rider profile already exists");
        }

        Rider rider = mapper.toRider(userId);
        riderRepository.save(rider);

        if (locationRequest != null) {
            try {
                client.createRiderLocation(
                        token,
                        rider.getId(),
                        locationRequest.status(),
                        locationRequest.latitude(),
                        locationRequest.longitude()
                );
            } catch (Exception e) {
                log.warn("Failed to create rider location for riderId={}: {}", rider.getId(), e.getMessage());
            }
        }

        return mapper.fromRiderToRiderResponse(rider);
    }


    public RiderResponse changeRiderStatus(String token, UUID id, RiderStatus status) {

        Rider rider = riderRepository.findById(id)
                .orElseThrow(() -> new RiderNotFoundException("Rider with id " + id + " not found"));
        rider.setRiderStatus(status);
        riderRepository.save(rider);

        try {
            client.updateRiderStatus(token, id, status);
        } catch (Exception e) {
            log.warn("Failed to update rider status in tracking service for riderId={}: {}", id, e.getMessage());
        }

        return mapper.fromRiderToRiderResponse(rider);
    }


    public RiderResponse getRiderProfile(UUID id) {

        Rider rider = riderRepository.findById(id)
                .orElseThrow(() -> new RiderNotFoundException("Rider with id " + id + " not found"));

        return mapper.fromRiderToRiderResponse(rider);
    }


    public DeliveryResponse findDeliveryById(UUID id) {

        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery with id " + id + " not found"));

        return mapper.fromDeliveryToDeliveryResponse(delivery);
    }

    public Page<DeliveryResponse> findRiderDeliveries(UUID riderId,
                                                      int page, int size, String sortBy) {
        riderRepository.findById(riderId)
                .orElseThrow(() -> new RiderNotFoundException("Rider with id " + riderId + " not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        return deliveryRepository.findByRiderId(riderId, pageable)
                .map(mapper::fromDeliveryToDeliveryResponse);
    }



    @Transactional
    public void assignDelivery(OrderCreatedEvent event) {
        log.info("Assigning delivery for orderId={}", event.id());

        if (deliveryRepository.findByOrderId(event.id()).isPresent()) {
            log.info("Delivery for orderId={} already exists, skipping assignment", event.id());
            return;
        }

        log.info("Fetching restaurant location for restaurantId={}", event.restaurantId());
        RestaurantResponse restaurantResponse = client.getRestaurantLocation(event.restaurantId());
        log.info("Fetched restaurant location: lat={}, lng={}", restaurantResponse.latitude(), restaurantResponse.longitude());

        RiderLocationResponse riderLocationResponse = client.getNearestRiderLocation(restaurantResponse.latitude(),
                restaurantResponse.longitude());

        if (riderLocationResponse == null || riderLocationResponse.riderId() == null) {
            log.warn("No rider found for orderId={}", event.id());
            return;
        }

        log.info("Found nearest rider: riderId={}, distanceKm={}", riderLocationResponse.riderId(), riderLocationResponse.distanceKm());

        Rider rider = riderRepository.findById(riderLocationResponse.riderId())
                .orElseThrow(() -> new RiderNotFoundException("Rider with id " + riderLocationResponse.riderId() + " not found"));

        Delivery delivery = mapper.fromOrderCreatedEventToDelivery(event);
        delivery.setRider(rider);
        rider.setRiderStatus(RiderStatus.BUSY);
        riderRepository.save(rider);
        deliveryRepository.save(delivery);

        log.info("Assigned riderId={} to deliveryId={}", rider.getId(), delivery.getId());

        webSocketHandler.sendToRider(rider.getId().toString(),
                "{\"event\":\"NEW_ORDER\",\"deliveryId\":\"" + delivery.getId() + "\"}");
    }


    public void acceptDelivery(UUID deliveryId, String riderId){
        Delivery delivery = getDeliveryAndVerifyRider(deliveryId, riderId);

        if (delivery.getRider() == null) {
            throw new IllegalStateException("Delivery has no rider assigned");
        }

        delivery.setDeliveryStatus(DeliveryStatus.ACCEPTED);
        Rider rider = delivery.getRider();
        rider.setRiderStatus(RiderStatus.BUSY);

        deliveryRepository.save(delivery);
        riderRepository.save(rider);

        kafkaProducerService.sendDeliveryAccepted(mapper.fromDeliveryToDeliveryAcceptedEvent(delivery));


    }

    public void declineDelivery(UUID deliveryId, String riderId) {
        Delivery delivery = getDeliveryAndVerifyRider(deliveryId, riderId);

        Rider oldRider = delivery.getRider();
        if (oldRider != null) {
            oldRider.setRiderStatus(RiderStatus.ONLINE);
        }

        delivery.setDeliveryStatus(DeliveryStatus.DECLINED);
        delivery.setRider(null);
        deliveryRepository.save(delivery);

        riderRepository.findFirstByRiderStatus(RiderStatus.ONLINE).ifPresent(rider -> {
            delivery.setRider(rider);
            rider.setRiderStatus(RiderStatus.BUSY);
            deliveryRepository.save(delivery);

            webSocketHandler.sendToRider(rider.getId().toString(),
                    "{\"event\":\"NEW_ORDER\",\"deliveryId\":\"" + delivery.getId() + "\"}");
        });
    }

    public void pickupDelivery(UUID deliveryId, String riderId) {
        Delivery delivery = getDeliveryAndVerifyRider(deliveryId, riderId);

        delivery.setDeliveryStatus(DeliveryStatus.PICKED_UP);
        delivery.setAssignedAt(Instant.now());
        deliveryRepository.save(delivery);
    }

    public void completeDelivery(UUID deliveryId, String riderId) {
        Delivery delivery = getDeliveryAndVerifyRider(deliveryId, riderId);

        delivery.setDeliveryStatus(DeliveryStatus.DELIVERED);
        delivery.setCompletedAt(Instant.now());

        Rider rider = delivery.getRider();
        rider.setRiderStatus(RiderStatus.ONLINE);

        deliveryRepository.save(delivery);
        riderRepository.save(rider);

        kafkaProducerService.sendDeliveryCompleted(mapper.fromDeliveryToDeliveryCompletedEvent(delivery));
    }

    private Delivery getDeliveryAndVerifyRider(UUID deliveryId, String riderId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery with id " + deliveryId + " not found"));

        if (delivery.getRider() == null || !delivery.getRider().getId().toString().equals(riderId)) {
            throw new IllegalStateException("Delivery is not assigned to this rider");
        }

        return delivery;
    }


    public void cancelDelivery(UUID orderId) {
        Optional<Delivery> optionalDelivery = deliveryRepository.findByOrderId(orderId);
        if (optionalDelivery.isEmpty()) {
            log.info("No delivery found for orderId={}, skipping cancellation", orderId);
            return;
        }

        Delivery delivery = optionalDelivery.get();
        delivery.setDeliveryStatus(DeliveryStatus.CANCELLED);

        if (delivery.getRider() != null) {
            delivery.getRider().setRiderStatus(RiderStatus.ONLINE);
            webSocketHandler.sendToRider(delivery.getRider().getId().toString(),
                    "{\"event\":\"ORDER_CANCELLED\",\"deliveryId\":\"" + delivery.getId() + "\"}");
        }

        deliveryRepository.save(delivery);
    }


}
