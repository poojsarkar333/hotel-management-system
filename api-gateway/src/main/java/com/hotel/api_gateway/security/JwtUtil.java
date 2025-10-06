package com.hotel.api_gateway.security;

import java.security.Key;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    
    private Key key;

    @PostConstruct
    public void init() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("Missing jwt.secret property");
        }
        if (secret.length() < 32) {
            throw new IllegalArgumentException("jwt.secret must be at least 32 characters long for HS256");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        Object roleObj = claims.get("role");
        if (roleObj == null) return List.of();
        if (roleObj instanceof String) return List.of((String) roleObj);
        if (roleObj instanceof List<?> roles) {
            return roles.stream().map(Object::toString).collect(Collectors.toList());
        }
        return List.of();
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}

