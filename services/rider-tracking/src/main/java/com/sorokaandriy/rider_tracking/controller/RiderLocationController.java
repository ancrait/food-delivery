package com.sorokaandriy.rider_tracking.controller;

import com.sorokaandriy.rider_tracking.dto.RiderLocationRequest;
import com.sorokaandriy.rider_tracking.dto.RiderLocationResponse;
import com.sorokaandriy.rider_tracking.service.RiderLocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tracking")
public class RiderLocationController {

    private final RiderLocationService service;

    @PostMapping("/location")
    @PreAuthorize("hasRole('RIDER')")
    public ResponseEntity<RiderLocationResponse> updateLocation(
            @Valid @RequestBody RiderLocationRequest request
    ) {
        return ResponseEntity.ok(service.updateLocation(request));
    }

    @GetMapping("/nearest")
    public ResponseEntity<RiderLocationResponse> findNearestRiderLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude
    ){
        return ResponseEntity.ok(service.findNearestRiderLocation(latitude, longitude));
    }
}
