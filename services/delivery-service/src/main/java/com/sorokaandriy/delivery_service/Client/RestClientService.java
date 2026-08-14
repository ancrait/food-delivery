package com.sorokaandriy.delivery_service.Client;

import com.sorokaandriy.delivery_service.dto.OrderResponse;
import com.sorokaandriy.delivery_service.dto.RestaurantResponse;
import com.sorokaandriy.delivery_service.dto.RiderLocationCreateRequest;
import com.sorokaandriy.delivery_service.dto.RiderLocationResponse;
import com.sorokaandriy.delivery_service.entity.RiderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
@Slf4j
public class RestClientService {

    private final RestClient client;
    private String riderTrackingUrl;
    private String restaurantUrl;
    private String orderUrl;

    public RestClientService(@Value("${app.rider-tracking-service.url}") String riderTrackingUrl,
                             @Value("${app.restaurant-service.url}") String restaurantUrl,
                             @Value("${app.order-service.url}") String orderUrl){
        this.client = RestClient.create();
        this.riderTrackingUrl = riderTrackingUrl;
        this.restaurantUrl = restaurantUrl;
        this.orderUrl = orderUrl;
    }

    public RiderLocationResponse getNearestRiderLocation(Double lat, Double lng) {
        try {
            return client.get()
                    .uri(riderTrackingUrl + "/nearest?latitude={lat}&longitude={lng}",
                            lat, lng)
                    .retrieve()
                    .body(RiderLocationResponse.class);
        } catch (Exception e) {
            log.error("Failed to get nearest rider location from {}: {}", riderTrackingUrl, e.getMessage());
            throw e;
        }
    }

    public RestaurantResponse getRestaurantLocation(UUID id) {
        try {
            return client.get()
                    .uri(restaurantUrl + "/{id}",
                            id)
                    .retrieve()
                    .body(RestaurantResponse.class);
        } catch (Exception e) {
            log.error("Failed to get restaurant location from {} for id={}: {}", restaurantUrl, id, e.getMessage());
            throw e;
        }
    }

    public OrderResponse getOrder(UUID id) {
        try {
            return client.get()
                    .uri(orderUrl + "/{id}",
                            id)
                    .retrieve()
                    .body(OrderResponse.class);
        } catch (Exception e) {
            log.error("Failed to get order from {} for id={}: {}", orderUrl, id, e.getMessage());
            throw e;
        }
    }

    public RiderLocationResponse createRiderLocation(String token, UUID riderId, RiderStatus status, Double latitude, Double longitude) {
        try {
            return client.post()
                    .uri(riderTrackingUrl + "/location")
                    .header("Authorization", "Bearer " + token)
                    .body(new RiderLocationCreateRequest(riderId, status, latitude, longitude))
                    .retrieve()
                    .body(RiderLocationResponse.class);
        } catch (Exception e) {
            log.error("Failed to create rider location at {} for riderId={}: {}", riderTrackingUrl, riderId, e.getMessage());
            throw e;
        }
    }

    public RiderLocationResponse updateRiderStatus(String token, UUID riderId, RiderStatus status) {
        try {
            return client.put()
                    .uri(riderTrackingUrl + "/location/{riderId}/status?status={status}", riderId, status)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(RiderLocationResponse.class);
        } catch (Exception e) {
            log.error("Failed to update rider status at {} for riderId={}: {}", riderTrackingUrl, riderId, e.getMessage());
            throw e;
        }
    }


}
