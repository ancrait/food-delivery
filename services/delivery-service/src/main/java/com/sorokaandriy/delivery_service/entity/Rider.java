package com.sorokaandriy.delivery_service.entity;

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
@Table(name = "riders")
public class Rider {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "user_id", unique = true, nullable = false)
    private UUID userId;
    @Column(name = "rider_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RiderStatus riderStatus;
    @Column(nullable = false)
    private BigDecimal rating;
    @Column(nullable = false, name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
    @Column(nullable = false, name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "rider")
    private List<Delivery> deliveries;
}
