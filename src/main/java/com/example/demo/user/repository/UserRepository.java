package com.example.demo.user.repository;

import com.example.demo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 엔티티를 위한 JPA 리포지토리.
 * 현재는 기본 CRUD 메서드와 이메일 조회 메서드를 제공합니다.
 */
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}


