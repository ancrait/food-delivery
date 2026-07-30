package com.sorokaandriy.order_service.dto;

import com.sorokaandriy.order_service.entity.OrderItem;
import com.sorokaandriy.order_service.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderRequest(
        @NotNull
        UUID restaurantId,
        @NotNull @Size(max = 256)
        String deliveryAddress,
        @NotBlank @Pattern(regexp = "^\\+?[0-9]{10,15}$")
        String phone,
        String notes,
        @NotNull @Size(min = 1)
        List<OrderItemRequest> orderItems

) {
}
