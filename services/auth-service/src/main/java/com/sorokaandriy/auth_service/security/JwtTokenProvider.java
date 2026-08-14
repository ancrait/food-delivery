package com.sorokaandriy.auth_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final long verificationTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration,
            @Value("${jwt.verification-token-expiration}") long verificationTokenExpiration) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        byte[] hs256Key = new byte[32];
        System.arraycopy(keyBytes, 0, hs256Key, 0, Math.min(keyBytes.length, 32));
        this.key = new SecretKeySpec(hs256Key, "HmacSHA256");
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.verificationTokenExpiration = verificationTokenExpiration;
    }

    public String generateAccessToken(String userId, String email, List<String> roles) {
        Date now = new Date();
        return Jwts.builder()
                .issuer("http://localhost:8081")
                .subject(userId)
                .claim("email", email)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenExpiration))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(String userId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshTokenExpiration))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String generateVerificationToken(String userId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + verificationTokenExpiration))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    // verify token and give claims
    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
