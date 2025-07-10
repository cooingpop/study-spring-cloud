package com.example.configclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RefreshScope
public class ConfigController {

    @Value("${spring.application.name:config-client}")
    private String applicationName;

    @Value("${server.port:8080}")
    private String serverPort;

    // This property should be defined in the config server
    @Value("${message:Default Message}")
    private String message;

    @GetMapping("/config")
    public Map<String, String> getConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("applicationName", applicationName);
        config.put("serverPort", serverPort);
        config.put("message", message);
        return config;
    }
}