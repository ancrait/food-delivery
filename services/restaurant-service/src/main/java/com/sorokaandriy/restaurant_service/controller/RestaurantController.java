package com.sorokaandriy.restaurant_service.controller;

import com.sorokaandriy.restaurant_service.dto.MenuItemRequest;
import com.sorokaandriy.restaurant_service.dto.MenuItemResponse;
import com.sorokaandriy.restaurant_service.dto.RestaurantRequest;
import com.sorokaandriy.restaurant_service.dto.RestaurantResponse;
import com.sorokaandriy.restaurant_service.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService service;


    @GetMapping
    public ResponseEntity<Page<RestaurantResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return ResponseEntity.ok(service.findAll(page, size, sortBy));
    }


    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> findRestaurantById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(service.findRestaurantById(id));
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestaurantResponse> createRestaurant(
            @Valid @RequestBody RestaurantRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createRestaurant(request));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @PathVariable UUID id,
            @Valid @RequestBody RestaurantRequest request
    ) {
        return ResponseEntity.ok(service.updateRestaurant(request, id));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRestaurant(
            @PathVariable UUID id
    ) {
        service.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/menu")
    public ResponseEntity<List<MenuItemResponse>> findAllMenuItemFromRestaurant(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(service.findAllMenuItemFromRestaurant(id));
    }


    @PostMapping("/{id}/menu")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuItemResponse> createMenuItem(
            @PathVariable UUID id,
            @Valid @RequestBody MenuItemRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createMenuItem(id, request));
    }


    @PutMapping("/{id}/menu/{idItem}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable UUID id,
            @Valid @RequestBody MenuItemRequest request,
            @PathVariable UUID idItem

    ) {
        return ResponseEntity.ok(service.updateMenuItem(id, request, idItem));
    }

    @DeleteMapping("/{id}/menu/{idItem}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMenuItem(
            @PathVariable UUID id,
            @PathVariable UUID idItem

    ){
        service.deleteMenuItem(id, idItem);
        return ResponseEntity.noContent().build();
    }

}

