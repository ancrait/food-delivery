package com.sorokaandriy.payment_service.service;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class StripeService {

    @Value("${stripe.secret-key}")
    private String secretKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public String createPaymentIntent(BigDecimal amount, String currency) {
        long amountInCents = amount.movePointRight(2).longValue();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(currency.toLowerCase())
                .build();

        try {
            PaymentIntent intent = PaymentIntent.create(params);
            log.info("Created PaymentIntent: {}", intent.getId());
            return intent.getId();
        } catch (Exception e) {
            log.error("Failed to create PaymentIntent: {}", e.getMessage());
            throw new RuntimeException("Stripe payment creation failed", e);
        }
    }

    public String getClientSecret(String paymentIntentId) {
        try {
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
            return intent.getClientSecret();
        } catch (Exception e) {
            log.error("Failed to retrieve PaymentIntent: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve Stripe payment", e);
        }
    }

    public boolean isPaymentSuccessful(String payload, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            return "payment_intent.succeeded".equals(event.getType());
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe webhook signature");
            return false;
        }
    }
}
