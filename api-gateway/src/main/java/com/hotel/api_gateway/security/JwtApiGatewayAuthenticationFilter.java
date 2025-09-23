package com.hotel.api_gateway.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private Environment env; // gets values from Config Server

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        List<String> userRoles = jwtUtil.extractRoles(token);
        String serviceKey = resolveServiceKey(path);

        if (serviceKey != null) {
            String allowedRolesStr = env.getProperty("roles.access." + serviceKey, "");
            List<String> allowedRoles = Arrays.asList(allowedRolesStr.split(","));

            boolean authorized = userRoles.stream().anyMatch(allowedRoles::contains);
            if (!authorized) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        }

        // Forward with user info
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("X-Auth-User", jwtUtil.extractUsername(token))
                .header("X-Auth-Roles", String.join(",", userRoles))
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    // Map path prefix to service keys in config
    private String resolveServiceKey(String path) {
        if (path.startsWith("/staff")) return "staff-service";
        if (path.startsWith("/rooms")) return "room-service";
        if (path.startsWith("/orders")) return "order-service";
        if (path.startsWith("/billing")) return "billing-service";
        return null; // fallback for open endpoints
    }

}




