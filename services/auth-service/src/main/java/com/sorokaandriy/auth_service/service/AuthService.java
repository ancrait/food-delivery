package com.sorokaandriy.auth_service.service;

import com.sorokaandriy.auth_service.dto.*;
import com.sorokaandriy.auth_service.entity.RefreshToken;
import com.sorokaandriy.auth_service.entity.Role;
import com.sorokaandriy.auth_service.entity.User;
import com.sorokaandriy.auth_service.entity.VerificationToken;
import com.sorokaandriy.auth_service.repository.RefreshTokenRepository;
import com.sorokaandriy.auth_service.repository.RoleRepository;
import com.sorokaandriy.auth_service.repository.UserRepository;
import com.sorokaandriy.auth_service.repository.VerificationTokenRepository;
import com.sorokaandriy.auth_service.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already taken");
        }

        Role role = roleRepository.findByName("ROLE_" + request.getRole().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + request.getRole()));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .phone(request.getPhone())
                .isEnabled(false)
                .roles(Set.of(role))
                .build();

        userRepository.save(user);

        String verificationToken = jwtTokenProvider.generateVerificationToken(user.getId().toString());

        VerificationToken token = VerificationToken.builder()
                .user(user)
                .token(verificationToken)
                .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
                .build();
        verificationTokenRepository.save(token);

        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .verificationToken(verificationToken)
                .firstName(user.getFirstname())
                .lastName(user.getLastname())
                .build();
        kafkaProducerService.sendUserRegistered(event);
    }



    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!user.isEnabled()) {
            throw new IllegalArgumentException("Email not verified");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId().toString(), user.getEmail(), roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId().toString());

        saveRefreshToken(user, refreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }



    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String rawToken = request.refreshToken();

        Claims claims;
        try {
            claims = jwtTokenProvider.validateToken(rawToken);
        } catch (JwtException e) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        UUID userId = UUID.fromString(claims.getSubject());

        RefreshToken existingToken = refreshTokenRepository.findByToken(rawToken)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (!existingToken.getUser().getId().equals(userId)) {
            refreshTokenRepository.delete(existingToken);
            throw new BadCredentialsException("Invalid refresh token");
        }

        refreshTokenRepository.delete(existingToken);

        User user = existingToken.getUser();
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId().toString(), user.getEmail(), roles);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId().toString());

        saveRefreshToken(user, newRefreshToken);

        return new AuthResponse(accessToken, newRefreshToken);
    }


    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {

        String tokenRequest = request.token();

        Claims claims;
        try {
            claims = jwtTokenProvider.validateToken(tokenRequest);
        } catch (JwtException e) {
            throw new BadCredentialsException("Invalid verify token");
        }

        UUID userId = UUID.fromString(claims.getSubject());

        VerificationToken verificationToken = verificationTokenRepository.findByToken(tokenRequest)
                .orElseThrow(() -> new BadCredentialsException("Invalid verification token"));

        if (!verificationToken.getUser().getId().equals(userId)) {
            verificationTokenRepository.delete(verificationToken);
            throw new BadCredentialsException("Invalid verification token");
        }

        verificationToken.getUser().setEnabled(true);

        verificationTokenRepository.delete(verificationToken);

    }



    @Transactional
    public void logout(RefreshTokenRequest request) {
        String rawToken = request.refreshToken();

        try {
            Claims claims = jwtTokenProvider.validateToken(rawToken);
            UUID userId = UUID.fromString(claims.getSubject());

            refreshTokenRepository.findByToken(rawToken).ifPresent(stored -> {
                if (stored.getUser().getId().equals(userId)) {
                    refreshTokenRepository.delete(stored);
                }
            });
        } catch (JwtException ignored) {
        }
    }

    private void saveRefreshToken(User user, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();
        refreshTokenRepository.save(refreshToken);
    }


}
