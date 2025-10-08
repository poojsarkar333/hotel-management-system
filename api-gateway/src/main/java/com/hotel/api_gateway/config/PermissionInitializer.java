package com.hotel.api_gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class PermissionInitializer {

    @Autowired
    private ConfigLoaderService configLoaderService;

    @PostConstruct
    public void init() {
        configLoaderService.loadAll();
    }
}

