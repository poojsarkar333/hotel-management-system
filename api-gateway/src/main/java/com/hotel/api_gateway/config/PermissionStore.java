package com.hotel.api_gateway.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Component
public class PermissionStore {

    /** List of all endpoint → action mappings */
    private final List<ActionMapping> actionMappings = new ArrayList<>();

    /** Role → allowed actions mapping */
    private final Map<String, Set<String>> roleToActions = new ConcurrentHashMap<>();

    private final AntPathMatcher matcher = new AntPathMatcher();

    // -----------------------
    // ActionMapping handling
    // -----------------------

    public void addActionMapping(ActionMapping mapping) {
        actionMappings.add(mapping);
    }

    public void clearActionMappings() {
        actionMappings.clear();
    }

    /**
     * Resolve the action key for a given path + HTTP method
     */
    public Optional<String> resolveAction(String path, String method) {
        for (ActionMapping m : actionMappings) {
            if (!m.getMethod().equalsIgnoreCase(method)) continue;
            if (matcher.match(m.getPathPattern(), path)) {
                return Optional.of(m.getAction());
            }
        }
        return Optional.empty();
    }

    /**
     * Check if a given role has permission to perform an action
     */
    public boolean roleHasAction(String role, String action) {
        Set<String> allowed = roleToActions.get(role.toUpperCase());
        return allowed != null && allowed.contains(action);
    }

    public void addRoleActions(String role, Collection<String> actions) {
        roleToActions.put(role.toUpperCase(), new HashSet<>(actions));
    }

    public void clearRoles() {
        roleToActions.clear();
    }

    public List<ActionMapping> getActionMappings() {
        return Collections.unmodifiableList(actionMappings);
    }

    public Map<String, Set<String>> getRoleToActions() {
        return Collections.unmodifiableMap(roleToActions);
    }

    // -----------------------
    // Inner class
    // -----------------------
    public static class ActionMapping {
        private String pathPattern;
        private String method;
        private String action;

        public ActionMapping() {}

        public ActionMapping(String pathPattern, String method, String action) {
            this.pathPattern = pathPattern;
            this.method = method;
            this.action = action;
        }

        public String getPathPattern() { return pathPattern; }
        public void setPathPattern(String pathPattern) { this.pathPattern = pathPattern; }

        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }

        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
    }
}



