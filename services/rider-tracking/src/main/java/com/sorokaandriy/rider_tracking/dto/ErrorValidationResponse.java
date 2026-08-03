package com.sorokaandriy.rider_tracking.dto;

import java.util.Map;

public record ErrorValidationResponse(
        Map<String,String> errors
) {
}
