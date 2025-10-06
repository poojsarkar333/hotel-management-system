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

	private String secret = "javajwttestabcdefghijklmnopqrstuvwxyzjwtjava"; // store safely in production

	private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }	
	
	// Generate token with roles
	public String generateToken(Staff staff) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", staff.getRole());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(staff.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

	// Extract username
	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}

	// Extract role(s)
	public List<String> extractRoles(String token) {
		Claims claims = extractAllClaims(token);
		Object rolesObj = claims.get("role");

		if (rolesObj instanceof String) {
			return List.of((String) rolesObj); // single role
		} else if (rolesObj instanceof List<?>) {
			return ((List<?>) rolesObj).stream().map(Object::toString).collect(Collectors.toList());
		}
		return List.of(); // fallback
	}

	// Validate token
	public boolean validateToken(String token) {
		try {
			extractAllClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
