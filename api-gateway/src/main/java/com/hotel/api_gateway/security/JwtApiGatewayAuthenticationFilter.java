package com.hotel.api_gateway.security;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class JwtApiGatewayAuthenticationFilter implements GlobalFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RestTemplate restTemplate;

    private Map<String,String> fetchRolesFromConfigServer() {
        try {
            String url = "http://localhost:8761/api-gateway/default";
            Map<?,?> response = restTemplate.getForObject(url, Map.class);
            List<Map<String,Object>> propertySources = (List<Map<String,Object>>) response.get("propertySources");
            Map<String,Object> source = propertySources.get(0).get("source") instanceof Map ? (Map<String,Object>)propertySources.get(0).get("source") : Map.of();

            Map<String,String> roles = new HashMap<>();
            source.forEach((k,v) -> {
                if(k.startsWith("roles.access.")) {
                    String serviceKey = k.replace("roles.access.","");
                    roles.put(serviceKey, v.toString());
                }
            });
            return roles;
        } catch(Exception e) {
            e.printStackTrace();
            return Map.of();
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Allow public endpoints
        if(path.startsWith("/auth") || path.startsWith("/actuator")) {
            return chain.filter(exchange);
        }

        // Extract token
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        // Validate JWT
        if(!jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        List<String> userRoles = jwtUtil.extractRoles(token);
        if(userRoles == null) userRoles = List.of();

        // Map URL to service key
        String serviceKey = resolveServiceKey(path);
        if(serviceKey != null) {
            Map<String,String> rolesMap = fetchRolesFromConfigServer();
            String allowedRolesStr = rolesMap.get(serviceKey);

            if(allowedRolesStr == null || allowedRolesStr.isBlank()) {
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

            if(!authorized) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        }

        // Forward headers
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("X-Auth-User", jwtUtil.extractUsername(token))
                .header("X-Auth-Roles", String.join(",", userRoles))
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private String resolveServiceKey(String path) {
        if(path.startsWith("/staff")) return "staff-service";
        if(path.startsWith("/rooms")) return "room-service";
        if(path.startsWith("/orders")) return "order-service";
        if(path.startsWith("/billing")) return "billing-service";
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(err.getBytes(StandardCharsets.UTF_8))));
    }
}







