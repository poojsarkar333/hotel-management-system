package com.hotel.api_gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RoleAccessConfig.class)
public class RoleConfigBinding {
}

