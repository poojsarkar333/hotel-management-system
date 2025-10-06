package com.hotel.api_gateway.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@RefreshScope
@ConfigurationProperties(prefix = "roles.access")
public class RoleAccessConfig {
	
	private Map<String, String> access = new HashMap<>();

	public Map<String, String> getAccess() {
		return access;
	}

	public void setAccess(Map<String, String> access) {
		this.access = access;
	}

	@PostConstruct
	public void log() {
		System.out.println("Loaded RBAC map: " + access);
	}
}
