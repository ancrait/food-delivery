package com.sorokaandriy.payment_service.service;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
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

    public String extractPaymentIntentId(String payload, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            if (!"payment_intent.succeeded".equals(event.getType())) {
                return null;
            }
            StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);
            if (stripeObject instanceof PaymentIntent intent) {
                return intent.getId();
            }
            log.warn("Could not deserialize PaymentIntent from event");
            return null;
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe webhook signature");
            return null;
        } catch (Exception e) {
            log.error("Failed to process Stripe webhook: {}", e.getMessage());
            return null;
        }
    }

    public void refund(String paymentIntentId) {
        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .build();
            Refund refund = Refund.create(params);
            log.info("Refund created for PaymentIntent {}: {}", paymentIntentId, refund.getId());
        } catch (Exception e) {
            log.error("Failed to refund PaymentIntent {}: {}", paymentIntentId, e.getMessage());
            throw new RuntimeException("Stripe refund failed", e);
        }
    }
}
