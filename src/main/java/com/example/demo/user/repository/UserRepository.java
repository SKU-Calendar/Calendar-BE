// src/main/java/com/example/demo/users/repository/UserRepository.java
package com.example.demo.users.repository;

import com.example.demo.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
