package com.sorokaandriy.order_service.exception;

public class OrderCannotChangeStatus extends RuntimeException {
    public OrderCannotChangeStatus(String message) {
        super(message);
    }
}
