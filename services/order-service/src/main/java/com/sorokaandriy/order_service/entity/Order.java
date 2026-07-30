package com.sorokaandriy.order_service.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;
    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;
    @Column(nullable = false, length = 20)
    private String phone;
    private String notes;
    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

}
