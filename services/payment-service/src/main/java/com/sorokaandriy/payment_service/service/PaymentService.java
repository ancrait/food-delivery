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
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final PaymentProducer paymentProducer;
    private final StripeService stripeService;


    public void createPayment(OrderCreatedEvent event) {
        repository.save(mapper.fromOrderCreatedEventToPayment(event));
    }


    public PaymentResponse getPayment(UUID id) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment with id " + id + " not found"));
        return mapper.fromPaymentToPaymentResponse(payment);
    }


    public PaymentPayResponse pay(CreatedPaymentRequest request) {
        Payment payment = repository.findByOrderId(request.orderId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment with order id " + request.orderId() + " not found"));

        String paymentIntentId = stripeService.createPaymentIntent(payment.getAmount(), payment.getCurrency());
        payment.setStripePaymentId(paymentIntentId);
        repository.save(payment);

        String clientSecret = stripeService.getClientSecret(paymentIntentId);

        return new PaymentPayResponse(clientSecret, mapper.fromPaymentToPaymentResponse(payment));
    }


    public void handleWebhook(String payload, String sigHeader) {
        if (stripeService.isPaymentSuccessful(payload, sigHeader)) {
            // extract paymentIntentId from payload
            String paymentIntentId = extractPaymentIntentId(payload);
            Payment payment = repository.findByStripePaymentId(paymentIntentId)
                    .orElseThrow(() -> new PaymentNotFoundException("Payment not found for stripe id: " + paymentIntentId));

            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            repository.save(payment);

            paymentProducer.sendPaymentSuccess(mapper.fromPaymentToPaymentSuccessEvent(payment));
        }
    }


    public void cancelPayment(UUID orderId) {
        Payment payment = repository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment with order id " + orderId + " not found"));

        payment.setPaymentStatus(PaymentStatus.CANCELLED);

        paymentProducer.sendPaymentCancel(mapper.fromPaymentToPaymentCancelEvent(payment));
        repository.save(payment);
    }

    private String extractPaymentIntentId(String payload) {
        return payload.contains("\"id\": \"pi_") 
                ? payload.split("\"id\": \"pi_")[1].split("\"")[0] 
                : null;
    }
}
