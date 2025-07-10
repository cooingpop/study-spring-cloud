package com.example.configclient;

import com.example.configclient.entity.User;
import com.example.configclient.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RefreshScope
public class ConfigController {

    private final UserRepository userRepository;

    @Value("${spring.application.name:config-client}")
    private String applicationName;

    @Value("${server.port:8080}")
    private String serverPort;

    // 이 속성은 config 서버에 정의되어야 합니다
    @Value("${message:Default Message}")
    private String message;

    public ConfigController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/config")
    public Map<String, String> getConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("applicationName", applicationName);
        config.put("serverPort", serverPort);
        config.put("message", message);
        return config;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
