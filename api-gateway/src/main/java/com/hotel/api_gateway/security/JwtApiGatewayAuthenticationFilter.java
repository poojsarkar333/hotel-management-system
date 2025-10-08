package com.hotel.api_gateway.security;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.hotel.api_gateway.config.PermissionStore;

import reactor.core.publisher.Mono;

@Component
public class JwtApiGatewayAuthenticationFilter implements GlobalFilter, Ordered {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private PermissionStore permissionStore;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		ServerHttpRequest request = exchange.getRequest();
		String path = request.getURI().getPath();
		String method = Optional.ofNullable(request.getMethod()).map(HttpMethod::name).orElse("");
		// Allow public endpoints
		if (path.startsWith("/auth") || path.startsWith("/actuator")) {
			return chain.filter(exchange);
		}

		// Extract token
		String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
		}

		String token = authHeader.substring(7);

		// Validate JWT
		if (!jwtUtil.validateToken(token)) {
			return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
		}

		List<String> userRoles = jwtUtil.extractRoles(token);
		if (userRoles == null)
			userRoles = List.of();

		// Resolve action for this request
		Optional<String> optAction = permissionStore.resolveAction(path, method);
		if (optAction.isEmpty()) {
			// No action mapped -> optionally allow or deny
			return onError(exchange, "No permission mapping for this endpoint", HttpStatus.FORBIDDEN);
		}

		String action = optAction.get();

		// Check if user role has access
		boolean authorized = userRoles.stream().map(r -> r.replace("ROLE_", "").trim().toUpperCase())
				.anyMatch(role -> permissionStore.roleHasAction(role, action));

		if (!authorized) {
			return onError(exchange, "Forbidden: insufficient permissions", HttpStatus.FORBIDDEN);
		}

		// Forward headers
		ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
				.header("X-Auth-User", jwtUtil.extractUsername(token))
				.header("X-Auth-Roles", String.join(",", userRoles)).header(HttpHeaders.AUTHORIZATION, authHeader)
				.build();

		return chain.filter(exchange.mutate().request(modifiedRequest).build());
	}

	private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
		exchange.getResponse().setStatusCode(status);
		return exchange.getResponse().writeWith(
				Mono.just(exchange.getResponse().bufferFactory().wrap(err.getBytes(StandardCharsets.UTF_8))));
	}

	@Override
	public int getOrder() {
		// run early in filter chain
		return -100;
	}

}
