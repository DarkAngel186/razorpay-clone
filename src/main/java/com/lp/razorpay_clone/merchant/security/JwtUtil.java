package com.lp.razorpay_clone.merchant.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String secretKey;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAcsessToken(String email, UUID merchantId, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim("merchant_id", merchantId)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3600))) // 1 hour expiration
                .signWith(getSecretKey())
                .compact();
    }

    public Claims verifyAcsessToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractRoles(Claims claims) {
        return claims.get("role", String.class);
    }

    public UUID extractMerchantId(Claims claims) {
        return UUID.fromString(claims.get("merchant_id", String.class));
    }
}
