package com.example.configclient.service;

import com.example.configclient.entity.User;
import com.example.configclient.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class DataInitializationService implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataInitializationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // Initialize sample data
        if (userRepository.count() == 0) {
            userRepository.save(new User("admin", "admin@example.com"));
            userRepository.save(new User("user1", "user1@example.com"));
            userRepository.save(new User("user2", "user2@example.com"));
            
            System.out.println("Sample users initialized in the database");
        }
    }
}