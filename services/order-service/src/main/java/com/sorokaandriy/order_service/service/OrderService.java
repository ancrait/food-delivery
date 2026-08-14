package com.sorokaandriy.order_service.service;

import com.sorokaandriy.order_service.client.MenuItemClient;
import com.sorokaandriy.order_service.dto.MenuItemResponse;
import com.sorokaandriy.order_service.dto.OrderRequest;
import com.sorokaandriy.order_service.dto.OrderResponse;
import com.sorokaandriy.order_service.entity.Order;
import com.sorokaandriy.order_service.entity.OrderItem;
import com.sorokaandriy.order_service.entity.Status;
import com.sorokaandriy.order_service.exception.OrderCannotChangeStatus;
import com.sorokaandriy.order_service.exception.OrderNotFoundException;
import com.sorokaandriy.order_service.kafka.KafkaOrderProducer;
import com.sorokaandriy.order_service.repository.OrderItemRepository;
import com.sorokaandriy.order_service.repository.OrderRepository;
import com.sorokaandriy.order_service.service.mapper.OrderMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper mapper;
    private final MenuItemClient client;
    private final KafkaOrderProducer kafkaOrderProducer;


    public Page<OrderResponse> findAllOrders(int page, int size, String sortBy) {

        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        return orderRepository.findByUserId(userId, pageable)
                .map(order -> mapper.fromOrderToOrderResponse(order));
    }


    public OrderResponse createOrder(OrderRequest request) {

        Authentication authentication = SecurityContextHolder.
                getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());

        AtomicReference<BigDecimal> totalPrice = new AtomicReference<>(BigDecimal.ZERO);
        List<OrderItem> orderItems = new ArrayList<>();

        request.orderItems()
                        .stream().forEach(orderItemRequest -> {
                    MenuItemResponse menuItemResponse = client.getMenuItem(request.restaurantId(),
                            orderItemRequest.menuItemId());
                    totalPrice.set(totalPrice.get().add(menuItemResponse.price()
                            .multiply(BigDecimal.valueOf(orderItemRequest.quantity()))));

                    orderItems.add(mapper.fromMenuItemResponseToOrderItem(menuItemResponse,
                            orderItemRequest.menuItemId(), orderItemRequest.quantity()));

                });

        Order order = mapper
                .fromOrderRequestToOrder(request, userId, orderItems, totalPrice.get());

        orderItems.forEach(item -> item.setOrder(order));

        orderRepository.save(order);

        try {
            kafkaOrderProducer.sendOrderCreated(
                    mapper.fromOrderToOrderCreatedEvent(order));
        } catch (Exception e) {
            log.error("Failed to send order.created event: {}", e.getMessage());
        }

        return mapper.fromOrderToOrderResponse(order);

    }


    public OrderResponse findOrderById(UUID id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + id + " not found"));

        return mapper.fromOrderToOrderResponse(order);
    }


    @Transactional
    public OrderResponse updateOrderStatus(UUID id, Status status) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + id + " not found"));

        order.setStatus(status);
        orderRepository.save(order);

        return mapper.fromOrderToOrderResponse(order);
    }


    public OrderResponse cancelOrder(UUID id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + id + " not found"));

        if (order.getStatus() != Status.CREATED){
            throw new OrderCannotChangeStatus("Order with status " + order.getStatus() + " cannot be changed");
        }

        order.setStatus(Status.CANCELLED);
        orderRepository.save(order);

        kafkaOrderProducer.sendOrderCancel(mapper.fromOrderToOrderCancelledEvent(order));

        return mapper.fromOrderToOrderResponse(order);


    }
}
