package com.sorokaandriy.order_service.dto;

import java.util.Map;

public record ErrorValidationResponse(
        Map<String,String> errors
) {
}
