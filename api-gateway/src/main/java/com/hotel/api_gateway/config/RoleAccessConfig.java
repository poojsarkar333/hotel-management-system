package com.hotel.api_gateway.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "roles.access")
public class RoleAccessConfig {
	private Map<String, String> access = new HashMap<>();

	public Map<String, String> getAccess() {
		return access;
	}

	public void setAccess(Map<String, String> access) {
		this.access = access;
	}
}
