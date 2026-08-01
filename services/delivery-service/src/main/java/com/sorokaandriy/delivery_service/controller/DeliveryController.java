package com.sorokaandriy.delivery_service.controller;


import com.sorokaandriy.delivery_service.dto.DeliveryResponse;
import com.sorokaandriy.delivery_service.dto.RiderResponse;
import com.sorokaandriy.delivery_service.entity.RiderStatus;
import com.sorokaandriy.delivery_service.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryController {


    private final DeliveryService service;

    @PostMapping("/rider")
    @PreAuthorize("hasRole('RIDER')")
    public ResponseEntity<RiderResponse> createRiderProfile(){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createRiderProfile());
    }


    @PutMapping("/rider/{id}/status")
    @PreAuthorize("hasRole('RIDER')")
    public ResponseEntity<RiderResponse> changeRiderStatus(
            @PathVariable UUID id,
            @RequestParam RiderStatus status
            ){
        return ResponseEntity.ok(service.changeRiderStatus(id, status));
    }


    @GetMapping("/rider/profile/{id}")
    @PreAuthorize("hasAnyRole('RIDER','ADMIN')")
    public ResponseEntity<RiderResponse> findRiderProfile(
            @PathVariable UUID id
    ){
        return ResponseEntity.ok(service.getRiderProfile(id));
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RIDER','ADMIN')")
    public ResponseEntity<DeliveryResponse> findDeliveryById(
            @PathVariable UUID id
    ){
        return ResponseEntity.ok(service.findDeliveryById(id));
    }

    @GetMapping("/rider/{riderId}")
    @PreAuthorize("hasAnyRole('RIDER','ADMIN')")
    public ResponseEntity<Page<DeliveryResponse>> findRiderDeliveries(
            @PathVariable UUID riderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ){
        return ResponseEntity.ok(service.findRiderDeliveries(riderId, page, size,sortBy));
    }


}
