package com.hotel.api_gateway.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ConfigLoaderService {

	private final ConfigServerClient configServerClient;
	private final PermissionStore permissionStore;

    private final List<String> roles;

	@Autowired
    public ConfigLoaderService(ConfigServerClient client, PermissionStore store,
                               @Value("${permissions.roles}") String rolesStr) {
        this.configServerClient = client;
        this.permissionStore = store;
        this.roles = Arrays.stream(rolesStr.split(","))
                           .map(String::trim)
                           .filter(s -> !s.isEmpty())
                           .map(String::toLowerCase)
                           .toList();
    }

    @Cacheable(value = "permissionsCache")
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
			System.out.println("Roles loaded into cache: " + roles);

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
    
    @CacheEvict(value = "permissionsCache", allEntries = true)
    @Scheduled(fixedRate = 60 * 60 * 1000) // every 1 hour
    public void refreshCache() {
        loadAll();
    }
}
