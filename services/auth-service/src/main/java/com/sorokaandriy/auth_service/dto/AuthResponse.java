package com.sorokaandriy.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    @NotBlank
    private String accessToken;
    @NotBlank
    private String refreshToken;
}
