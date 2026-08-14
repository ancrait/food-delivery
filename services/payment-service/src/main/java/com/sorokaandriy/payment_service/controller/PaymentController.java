package com.sorokaandriy.payment_service.controller;

import com.sorokaandriy.payment_service.dto.CreatedPaymentRequest;
import com.sorokaandriy.payment_service.dto.PaymentPayResponse;
import com.sorokaandriy.payment_service.dto.PaymentResponse;
import com.sorokaandriy.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentPayResponse> pay(
            @Valid @RequestBody CreatedPaymentRequest request
    ) {
        return ResponseEntity.ok(service.pay(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(service.getPayment(id));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        service.handleWebhook(payload, sigHeader);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> confirmPayment(@PathVariable UUID id) {
        return ResponseEntity.ok(service.confirmPayment(id));
    }
}
