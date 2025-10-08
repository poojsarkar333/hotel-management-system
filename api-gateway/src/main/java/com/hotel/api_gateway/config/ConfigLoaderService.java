package com.hotel.api_gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConfigLoaderService {

	private final ConfigServerClient configServerClient;
	private final PermissionStore permissionStore;

	// roles to load - you could make this configurable
	private final List<String> roles = List.of("admin", "staff", "customer");

	@Autowired
	public ConfigLoaderService(ConfigServerClient client, PermissionStore store) {
		this.configServerClient = client;
		this.permissionStore = store;
	}

	public void loadAll() {
		// load roles
		permissionStore.clearRoles();
		for (String r : roles) {
			String name = "roles-" + r; // will fetch e.g. /roles-admin/default
			Map<String, Object> map = configServerClient.fetchConfigAsMap(name);
			// we expect key 'permissions.allowedActions' which maps to a list
			Set<String> actions = new HashSet<>();

			int i = 0;
			while (map.containsKey("permissions.allowedActions[" + i + "]")) {
				Object val = map.get("permissions.allowedActions[" + i + "]");
				if (val != null) {
					actions.add(val.toString().trim().toUpperCase());
				}
				i++;
			}

			// Fallback: if no indexed values found, try direct or comma-separated
			if (actions.isEmpty()) {
				Object maybe = map.get("permissions.allowedActions");
				if (maybe instanceof Collection) {
					for (Object o : (Collection<?>) maybe) {
						actions.add(String.valueOf(o).trim().toUpperCase());
					}
				} else if (maybe instanceof String) {
					String[] parts = ((String) maybe).split(",");
					for (String p : parts)
						actions.add(p.trim().toUpperCase());
				}
			}

			// Store it
			permissionStore.addRoleActions(r.toUpperCase(), actions);

		}

		// load actions mapping
		permissionStore.clearActionMappings();
		Map<String, Object> actionsMap = configServerClient.fetchConfigAsMap("actions");
		System.out.println("Actions source: " + actionsMap);
		int i = 0;
		while (actionsMap.containsKey("actions[" + i + "].pathPattern")) {
			String path = (String) actionsMap.get("actions[" + i + "].pathPattern");
			String method = (String) actionsMap.get("actions[" + i + "].method");
			String action = (String) actionsMap.get("actions[" + i + "].action");
			permissionStore.addActionMapping(new PermissionStore.ActionMapping(path, method, action));
			i++;
		}
	}
}
