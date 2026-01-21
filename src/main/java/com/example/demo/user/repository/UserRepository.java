package com.example.demo.user.repository;

import com.example.demo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // ✅ 기존 서비스(Calendar/Event/Auth/Chat/Timer 등)에서 사용 중 → 반드시 필요
    Optional<User> findByEmail(String email);

    // ✅ principal(String=email) -> userId(UUID) 변환용
    @Query("select u.id from User u where u.email = :email")
    Optional<UUID> findIdByEmail(String email);
}
