package com.sorokaandriy.payment_service.service;

import com.sorokaandriy.payment_service.dto.CreatedPaymentRequest;
import com.sorokaandriy.payment_service.dto.OrderCreatedEvent;
import com.sorokaandriy.payment_service.dto.PaymentPayResponse;
import com.sorokaandriy.payment_service.dto.PaymentResponse;
import com.sorokaandriy.payment_service.entity.Payment;
import com.sorokaandriy.payment_service.entity.PaymentStatus;
import com.sorokaandriy.payment_service.exception.PaymentNotFoundException;
import com.sorokaandriy.payment_service.kafka.PaymentProducer;
import com.sorokaandriy.payment_service.repository.PaymentRepository;
import com.sorokaandriy.payment_service.service.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final PaymentProducer paymentProducer;
    private final StripeService stripeService;

    @Transactional
    public void createPayment(OrderCreatedEvent event) {
        repository.findByOrderId(event.id()).ifPresentOrElse(
                existing -> log.info("Payment already exists for orderId={}", event.id()),
                () -> {
                    Payment payment = mapper.fromOrderCreatedEventToPayment(event);
                    repository.save(payment);
                    log.info("Created payment for orderId={}", event.id());
                }
        );
    }

    public PaymentResponse getPayment(UUID id) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment with id " + id + " not found"));
        return mapper.fromPaymentToPaymentResponse(payment);
    }

    @Transactional
    public PaymentPayResponse pay(CreatedPaymentRequest request) {
        Payment payment = repository.findByOrderId(request.orderId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment with order id " + request.orderId() + " not found"));

        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            log.info("Payment already succeeded for orderId={}", request.orderId());
            return new PaymentPayResponse(null, mapper.fromPaymentToPaymentResponse(payment));
        }

        if (payment.getStripePaymentId() == null) {
            String paymentIntentId = stripeService.createPaymentIntent(payment.getAmount(), payment.getCurrency());
            payment.setStripePaymentId(paymentIntentId);
        }

        repository.save(payment);

        String clientSecret = stripeService.getClientSecret(payment.getStripePaymentId());
        return new PaymentPayResponse(clientSecret, mapper.fromPaymentToPaymentResponse(payment));
    }

    @Transactional
    public void handleWebhook(String payload, String sigHeader) {
        String paymentIntentId = stripeService.extractPaymentIntentId(payload, sigHeader);
        if (paymentIntentId == null) {
            log.warn("No succeeded payment_intent in webhook payload");
            return;
        }

        Payment payment = repository.findByStripePaymentId(paymentIntentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for stripe id: " + paymentIntentId));

        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            log.info("Payment {} already marked as success", paymentIntentId);
            return;
        }

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        repository.save(payment);

        paymentProducer.sendPaymentSuccess(mapper.fromPaymentToPaymentSuccessEvent(payment));
    }

    @Transactional
    public PaymentResponse confirmPayment(UUID id) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment with id " + id + " not found"));

        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            return mapper.fromPaymentToPaymentResponse(payment);
        }

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        repository.save(payment);

        try {
            paymentProducer.sendPaymentSuccess(mapper.fromPaymentToPaymentSuccessEvent(payment));
        } catch (Exception e) {
            log.error("Failed to send payment.success event: {}", e.getMessage());
        }

        return mapper.fromPaymentToPaymentResponse(payment);
    }

    @Transactional
    public void cancelPayment(UUID orderId) {
        Payment payment = repository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment with order id " + orderId + " not found"));

        if (payment.getPaymentStatus() == PaymentStatus.CANCELLED
                || payment.getPaymentStatus() == PaymentStatus.REFUNDED) {
            log.info("Payment for orderId={} already cancelled/refunded", orderId);
            return;
        }

        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS
                && payment.getStripePaymentId() != null) {
            stripeService.refund(payment.getStripePaymentId());
            payment.setPaymentStatus(PaymentStatus.REFUNDED);
        } else {
            payment.setPaymentStatus(PaymentStatus.CANCELLED);
        }

        paymentProducer.sendPaymentCancel(mapper.fromPaymentToPaymentCancelEvent(payment));
        repository.save(payment);
    }
}
