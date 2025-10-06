package com.hotel.staff_service.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.hotel.staff_service.entity.Staff;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // Must match api-gateway secret
    private final String secret = "javajwttestabcdefghijklmnopqrstuvwxyzjwtjavajavajwttestabcdefghijklmnopqrstuvwxyzjwtjava";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Generate token with username + roles
    public String generateToken(Staff staff) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", staff.getRole());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(staff.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
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
        if (roleObj instanceof String) return List.of((String) roleObj);
        if (roleObj instanceof List<?> roles) return roles.stream().map(Object::toString).collect(Collectors.toList());
        return List.of();
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
