package com.sorokaandriy.order_service.client;

import com.sorokaandriy.order_service.dto.MenuItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
public class MenuItemClient {

    private final RestClient restClient;
    private final String menuItemUrl;

    public MenuItemClient(@Value("${app.restaurant-service.url}") String menuItemUrl){
        restClient = RestClient.create();
        this.menuItemUrl = menuItemUrl;
    }

    public MenuItemResponse getMenuItem(UUID id, UUID menuItemId){
        return restClient.get()
                .uri(menuItemUrl + "/{id}/menu/{menuItemId}", id, menuItemId)
                .retrieve()
                .body(MenuItemResponse.class);
    }

}
