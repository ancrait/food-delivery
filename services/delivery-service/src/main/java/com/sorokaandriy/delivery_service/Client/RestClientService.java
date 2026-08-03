package com.sorokaandriy.delivery_service.Client;

import com.sorokaandriy.delivery_service.dto.OrderResponse;
import com.sorokaandriy.delivery_service.dto.RestaurantResponse;
import com.sorokaandriy.delivery_service.dto.RiderLocationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
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
    }

    public RiderLocationResponse getNearestRiderLocation(Double lat, Double lng) {
        return client.get()
                .uri(riderTrackingUrl + "/nearest?latitude={lat}&longitude={lng}",
                        lat, lng)
                .retrieve()
                .body(RiderLocationResponse.class);
    }

    public RestaurantResponse getRestaurantLocation(UUID id) {
        return client.get()
                .uri(restaurantUrl + "/{id}",
                        id)
                .retrieve()
                .body(RestaurantResponse.class);
    }

    public OrderResponse getOrder(UUID id) {
        return client.get()
                .uri(orderUrl + "/{id}",
                        id)
                .retrieve()
                .body(OrderResponse.class);
    }


}
