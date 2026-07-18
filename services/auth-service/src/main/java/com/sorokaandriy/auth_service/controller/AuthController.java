package com.sorokaandriy.auth_service.controller;

import com.sorokaandriy.auth_service.dto.*;
import com.sorokaandriy.auth_service.entity.VerificationToken;
import com.sorokaandriy.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ){
        return ResponseEntity.ok(authService.refresh(request));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshTokenRequest request
    ){
        authService.logout(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @PostMapping("/verify")
    public ResponseEntity<Void> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request
            ){
        authService.verifyEmail(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }



}
