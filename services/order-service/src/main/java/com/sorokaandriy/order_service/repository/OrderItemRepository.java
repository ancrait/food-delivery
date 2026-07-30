package com.sorokaandriy.order_service.repository;

import com.sorokaandriy.order_service.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    OrderItem findByMenuItemId(UUID menuItemId);

    List<OrderItem> findAllByMenuItemId(UUID menuItemId);
}
