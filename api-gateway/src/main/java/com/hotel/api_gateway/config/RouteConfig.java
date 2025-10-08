package com.hotel.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {
	
	@Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("staff-service", r -> r.path("/staff/**")
                        .uri("lb://STAFF-SERVICE"))
                .route("room-service", r -> r.path("/rooms/**")
                        .uri("lb://ROOM-SERVICE"))
                .route("order-service", r -> r.path("/api/orders/**")
                        .uri("lb://ORDER-SERVICE"))
                .route("billing-service", r -> r.path("/billing/**")
                        .uri("lb://BILLING-SERVICE"))
                .build();
    }
}

