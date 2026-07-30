package com.sorokaandriy.order_service.service.mapper;

import com.sorokaandriy.order_service.dto.*;
import com.sorokaandriy.order_service.entity.Order;
import com.sorokaandriy.order_service.entity.OrderItem;
import com.sorokaandriy.order_service.entity.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderMapper {


    public OrderResponse fromOrderToOrderResponse(Order order) {
        List<OrderItemResponse> orderItems = Optional.ofNullable(order.getOrderItems())
                .orElse(Collections.emptyList())
                .stream().map(orderItem ->
                OrderItemResponse.builder()
                        .id(orderItem.getId())
                        .menuItemId(orderItem.getMenuItemId())
                        .name(orderItem.getName())
                        .price(orderItem.getPrice())
                        .quantity(orderItem.getQuantity())
                        .build()).toList();


        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .restaurantId(order.getRestaurantId())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .deliveryAddress(order.getDeliveryAddress())
                .phone(order.getPhone())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .orderItems(orderItems)
                .build();
    }



    public OrderItem fromMenuItemResponseToOrderItem(MenuItemResponse menuItemResponse,
                                                     UUID itemId, Integer quantity){

        return OrderItem.builder()
                .menuItemId(itemId)
                .name(menuItemResponse.name())
                .price(menuItemResponse.price())
                .quantity(quantity)
                .build();
    }

    public Order fromOrderRequestToOrder(OrderRequest request,
                                         UUID userId, List<OrderItem> orderItems, BigDecimal totalPrice) {

        return Order.builder()
                .userId(userId)
                .restaurantId(request.restaurantId())
                .status(Status.CREATED)
                .totalPrice(totalPrice)
                .deliveryAddress(request.deliveryAddress())
                .phone(request.phone())
                .notes(request.notes())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .orderItems(orderItems)
                .build();
    }

    public OrderCreatedEvent fromOrderToOrderCreatedEvent(Order order){

        List<OrderItemEvent> orderItemEvents = Optional.ofNullable(order.getOrderItems())
                .orElse(Collections.emptyList())
                .stream()
                .map(orderItem ->
                    OrderItemEvent.builder()
                            .menuItemId(orderItem.getMenuItemId())
                            .name(orderItem.getName())
                            .price(orderItem.getPrice())
                            .quantity(orderItem.getQuantity())
                            .build()
                ).toList();


        return OrderCreatedEvent.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .restaurantId(order.getRestaurantId())
                .totalPrice(order.getTotalPrice())
                .phone(order.getPhone())
                .deliveryAddress(order.getDeliveryAddress())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .orderItems(orderItemEvents)
                .build();
    }

    public OrderCancelledEvent fromOrderToOrderCancelledEvent(Order order) {
        return OrderCancelledEvent.builder()
                .userId(order.getUserId())
                .orderId(order.getId())
                .restaurantId(order.getRestaurantId())
                .build();
    }
}
