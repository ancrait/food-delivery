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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final RiderRepository riderRepository;
    private final DeliveryMapper mapper;
    private final KafkaProducerService kafkaProducerService;
    private final DeliveryWebSocketHandler webSocketHandler;
    private final RestClientService client;

    public RiderResponse createRiderProfile() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());

        if (riderRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("Rider profile already exists");
        }

        Rider rider = mapper.toRider(userId);
        riderRepository.save(rider);

        return mapper.fromRiderToRiderResponse(rider);
    }


    public RiderResponse changeRiderStatus(UUID id, RiderStatus status) {

        Rider rider = riderRepository.findById(id)
                .orElseThrow(() -> new RiderNotFoundException("Rider with id " + id + " not found"));
        rider.setRiderStatus(status);
        riderRepository.save(rider);

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



    public void assignDelivery(OrderCreatedEvent event) {
        Delivery delivery = mapper.fromOrderCreatedEventToDelivery(event);
        deliveryRepository.save(delivery);

        OrderResponse orderResponse = client.getOrder(delivery.getOrderId());
        RestaurantResponse restaurantResponse =
                client.getRestaurantLocation(orderResponse.restaurantId());
        RiderLocationResponse riderLocationResponse = client.getNearestRiderLocation(restaurantResponse.latitude(),
                restaurantResponse.longitude());

        if (riderLocationResponse != null && riderLocationResponse.riderId() != null) {
            Rider rider = riderRepository.findById(riderLocationResponse.riderId())
                    .orElseThrow(() -> new RiderNotFoundException("Rider not found"));

            delivery.setRider(rider);
            rider.setRiderStatus(RiderStatus.BUSY);
            deliveryRepository.save(delivery);

            webSocketHandler.sendToRider(rider.getId().toString(),
                    "{\"event\":\"NEW_ORDER\",\"deliveryId\":\"" + delivery.getId() + "\"}");
        }
    }


    public void acceptDelivery(UUID deliveryId){
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery with id " + deliveryId + " not found"));

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

    public void declineDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery with id " + deliveryId + " not found"));

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

    public void pickupDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery with id " + deliveryId + " not found"));

        delivery.setDeliveryStatus(DeliveryStatus.PICKED_UP);
        delivery.setAssignedAt(Instant.now());
        deliveryRepository.save(delivery);
    }

    public void completeDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery with id " + deliveryId + " not found"));

        delivery.setDeliveryStatus(DeliveryStatus.DELIVERED);
        delivery.setCompletedAt(Instant.now());

        Rider rider = delivery.getRider();
        rider.setRiderStatus(RiderStatus.ONLINE);

        deliveryRepository.save(delivery);
        riderRepository.save(rider);

        kafkaProducerService.sendDeliveryCompleted(mapper.fromDeliveryToDeliveryCompletedEvent(delivery));
    }


    public void cancelDelivery(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery with order id " + orderId + " not found"));

        delivery.setDeliveryStatus(DeliveryStatus.CANCELLED);

        if (delivery.getRider() != null) {
            delivery.getRider().setRiderStatus(RiderStatus.ONLINE);
            webSocketHandler.sendToRider(delivery.getRider().getId().toString(),
                    "{\"event\":\"ORDER_CANCELLED\",\"deliveryId\":\"" + delivery.getId() + "\"}");
        }

        deliveryRepository.save(delivery);
    }


}
