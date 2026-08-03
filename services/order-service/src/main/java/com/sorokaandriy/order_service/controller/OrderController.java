package com.sorokaandriy.order_service.controller;

import com.sorokaandriy.order_service.dto.OrderRequest;
import com.sorokaandriy.order_service.dto.OrderResponse;
import com.sorokaandriy.order_service.entity.Status;
import com.sorokaandriy.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<Page<OrderResponse>> findAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ){
        return ResponseEntity.ok(service.findAllOrders(page, size, sortBy));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request
            ){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createOrder(request));

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<OrderResponse> findOrderById(
            @PathVariable UUID id
            ){
        return ResponseEntity.ok(service.findOrderById(id));
    }


    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable UUID id,
            @RequestParam Status status
            ){
        return ResponseEntity.ok(service.updateOrderStatus(id, status));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable UUID id
    ){
        return ResponseEntity.ok(service.cancelOrder(id));
    }




}
