package com.example.configclient.repository;

import com.example.configclient.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA는 기본 CRUD 작업을 자동으로 구현합니다
    // 필요한 경우 사용자 정의 쿼리 메서드를 추가할 수 있습니다
}
