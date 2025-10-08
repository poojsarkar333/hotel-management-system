package com.hotel.api_gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/permissions")
public class PermissionRefreshController {

    private final ConfigLoaderService configLoaderService;

    @Autowired
    public PermissionRefreshController(ConfigLoaderService loader) {
        this.configLoaderService = loader;
    }

    @PostMapping("/refresh")
    public String refresh() {
        configLoaderService.loadAll();
        return "Permissions refreshed";
    }
}

