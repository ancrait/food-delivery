package com.sorokaandriy.payment_service.dto;

import java.util.Map;

public record ErrorValidationResponse(
        Map<String,String> errors
) {
}
