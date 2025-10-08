package com.hotel.api_gateway.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class ConfigServerClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${config.server.url:http://localhost:8761}")
    private String configServerUrl;

    public ConfigServerClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Fetches a config from config server for application name `name` and profile `default` and returns the
     * merged property source as a Map.
     */
    public Map<String, Object> fetchConfigAsMap(String name) {
        try {
            String url = String.format("%s/%s/%s", configServerUrl, name, "default");
            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
            if (resp.getStatusCode() == HttpStatus.OK && resp.getBody() != null) {
                JsonNode root = objectMapper.readTree(resp.getBody());
                // response contains propertySources array; last one is usually application-level properties
                JsonNode propertySources = root.path("propertySources");
                Map<String, Object> merged = new HashMap<>();
                if (propertySources.isArray()) {
                    for (JsonNode node : propertySources) {
                        JsonNode src = node.path("source");
                        if (src.isObject()) {
                            Iterator<Map.Entry<String, JsonNode>> fields = src.fields();
                            while (fields.hasNext()) {
                                Map.Entry<String, JsonNode> e = fields.next();
                                merged.put(e.getKey(), objectMapper.convertValue(e.getValue(), Object.class));
                            }
                        }
                    }
                }
                return merged;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch config " + name + " from Config Server at " + configServerUrl, e);
        }
        return Collections.emptyMap();
    }

}
