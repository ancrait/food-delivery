package com.sorokaandriy.delivery_service.dto;

import java.util.Map;

public record ErrorValidationResponse(
        Map<String,String> errors
) {
}
