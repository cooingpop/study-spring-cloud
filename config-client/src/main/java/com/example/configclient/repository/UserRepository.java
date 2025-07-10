package com.example.configclient.repository;

import com.example.configclient.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA will automatically implement basic CRUD operations
    // We can add custom query methods if needed
}