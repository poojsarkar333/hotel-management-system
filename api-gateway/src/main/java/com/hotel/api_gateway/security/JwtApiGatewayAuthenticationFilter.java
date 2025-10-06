package com.hotel.api_gateway.security;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class JwtApiGatewayAuthenticationFilter implements GlobalFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private Environment env;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1️⃣ Public endpoints (login, health)
        if (path.startsWith("/auth") || path.startsWith("/actuator")) {
            return chain.filter(exchange);
        }

        // 2️⃣ Extract Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        // 3️⃣ Validate JWT
        if (!jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 4️⃣ Extract roles from JWT
        List<String> userRoles = jwtUtil.extractRoles(token);
        if (userRoles == null) userRoles = List.of();

        // 5️⃣ Map path to service key
        String serviceKey = resolveServiceKey(path);
        if (serviceKey != null) {
            // 🔹 Read roles dynamically from Environment at request time
            String allowedRolesStr = env.getProperty("roles.access." + serviceKey, "");
            if (allowedRolesStr.isBlank()) {
                return onError(exchange, "Configuration missing for service", HttpStatus.FORBIDDEN);
            }

            List<String> allowedRoles = Arrays.stream(allowedRolesStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::toUpperCase)
                    .collect(Collectors.toList());

            List<String> normalizedUserRoles = userRoles.stream()
                    .map(r -> r.replace("ROLE_", "").trim().toUpperCase())
                    .toList();

            boolean authorized = normalizedUserRoles.stream()
                    .anyMatch(allowedRoles::contains);

            if (!authorized) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        }

        // 6️⃣ Forward user info and Authorization to downstream services
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("X-Auth-User", jwtUtil.extractUsername(token))
                .header("X-Auth-Roles", String.join(",", userRoles))
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    // Map URL prefix to service keys
    private String resolveServiceKey(String path) {
        if (path.startsWith("/staff")) return "staff-service";
        if (path.startsWith("/rooms")) return "room-service";
        if (path.startsWith("/orders")) return "order-service";
        if (path.startsWith("/billing")) return "billing-service";
        return null; // open endpoints
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(err.getBytes(StandardCharsets.UTF_8))));
    }
}





