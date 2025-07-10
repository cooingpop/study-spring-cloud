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
        // 샘플 데이터 초기화
        if (userRepository.count() == 0) {
            userRepository.save(new User("admin", "admin@example.com"));
            userRepository.save(new User("user1", "user1@example.com"));
            userRepository.save(new User("user2", "user2@example.com"));

            System.out.println("데이터베이스에 샘플 사용자가 초기화되었습니다");
        }
    }
}
