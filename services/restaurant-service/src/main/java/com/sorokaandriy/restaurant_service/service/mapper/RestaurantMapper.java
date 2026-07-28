package com.sorokaandriy.restaurant_service.service.mapper;

import com.sorokaandriy.restaurant_service.dto.*;
import com.sorokaandriy.restaurant_service.entity.Category;
import com.sorokaandriy.restaurant_service.entity.MenuItem;
import com.sorokaandriy.restaurant_service.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RestaurantMapper {

    public RestaurantResponse fromRestaurnatToRestaurantResponse(Restaurant restaurant) {
        List<MenuItemResponse> menuItemResponses = Optional.ofNullable(restaurant.getMenuItems())
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> new MenuItemResponse(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getPrice(),
                        item.getImageUrl(),
                        item.isAvailable(),
                        item.getCategory() != null ? item.getCategory().getName() : null
                ))
                .collect(Collectors.toList());

        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getDescription(),
                restaurant.getAddress(),
                restaurant.getPhone(),
                restaurant.getRating(),
                restaurant.getLogoUrl(),
                restaurant.isActive(),
                menuItemResponses
        );
    }

    public Restaurant fromRequestToResponseRestaurant(RestaurantRequest request) {
        return Restaurant.builder()
                .name(request.name())
                .description(request.description())
                .address(request.address())
                .phone(request.phone())
                .rating(request.rating())
                .logoUrl(request.logoUrl())
                .isActive(request.isActive())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .menuItems(null)
                .build();
    }


    public void updateEntityRestaurant(Restaurant restaurant, RestaurantRequest request){
        restaurant.setName(request.name());
        restaurant.setDescription(request.description());
        restaurant.setAddress(request.address());
        restaurant.setPhone(request.phone());
        restaurant.setRating(request.rating());
        restaurant.setLogoUrl(request.logoUrl());
        restaurant.setActive(request.isActive());
        restaurant.setUpdatedAt(Instant.now());
    }

    public List<MenuItemResponse> fromMenuItemToMenuItemResponse(Restaurant restaurant) {

        return Optional.ofNullable(restaurant.getMenuItems())
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> new MenuItemResponse(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getPrice(),
                        item.getImageUrl(),
                        item.isAvailable(),
                        item.getCategory() != null ? item.getCategory().getName() : null
                ))
                .collect(Collectors.toList());


    }

    public MenuItemResponse fromMenuItemToMenuItemResponse(MenuItem menuItem) {

        return MenuItemResponse.builder()
                .id(menuItem.getId())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .price(menuItem.getPrice())
                .imageUrl(menuItem.getImageUrl())
                .isAvailable(menuItem.isAvailable())
                .categoryName(menuItem.getCategory().getName())
                .build();


    }

    public MenuItem fromMenuItemRequestToMenuItem(Restaurant restaurant, MenuItemRequest request, Category category) {
        return MenuItem.builder()
                .restaurant(restaurant)
                .category(category)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .imageUrl(request.imageUrl())
                .isAvailable(request.isAvailable())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }


    public void updateMenuItem(MenuItem menuItem, MenuItemRequest request,
                               Category category){
        menuItem.setCategory(category);
        menuItem.setName(request.name());
        menuItem.setDescription(request.description());
        menuItem.setPrice(request.price());
        menuItem.setImageUrl(request.imageUrl());
        menuItem.setAvailable(request.isAvailable());
        menuItem.setUpdatedAt(Instant.now());
    }

    public CategoryResponse fromCategoryToCategoryResponse(Category category){
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
