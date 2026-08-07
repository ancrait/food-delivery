package com.sorokaandriy.payment_service.service.mapper;

import com.sorokaandriy.payment_service.dto.OrderCreatedEvent;
import com.sorokaandriy.payment_service.dto.PaymentCancelEvent;
import com.sorokaandriy.payment_service.dto.PaymentResponse;
import com.sorokaandriy.payment_service.dto.PaymentSuccessEvent;
import com.sorokaandriy.payment_service.entity.Payment;
import com.sorokaandriy.payment_service.entity.PaymentStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PaymentMapper {


    public Payment fromOrderCreatedEventToPayment(OrderCreatedEvent event) {

        return Payment.builder()
                .orderId(event.id())
                .userId(event.userId())
                .amount(event.totalPrice())
                .currency("UAH")
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(Instant.now())
                .build();
    }



    public PaymentSuccessEvent fromPaymentToPaymentSuccessEvent(Payment payment) {

        return PaymentSuccessEvent.builder()
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .paymentId(payment.getId())
                .amount(payment.getAmount())
                .build();
    }



    public PaymentResponse fromPaymentToPaymentResponse(Payment payment) {

        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentStatus(payment.getPaymentStatus())
                .createdAt(Instant.now())
                .build();
    }

    public PaymentCancelEvent fromPaymentToPaymentCancelEvent(Payment payment) {

        return PaymentCancelEvent.builder()
                .orderId(payment.getOrderId())
                .paymentId(payment.getId())
                .build();
    }
}
