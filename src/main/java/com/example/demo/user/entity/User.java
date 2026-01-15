package com.example.demo.user.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 사용자 정보를 나타내는 엔티티.
 *
 * users 테이블 구조:
 * - id (UUID, NOT NULL)
 * - email (VARCHAR(255), NOT NULL)
 * - password (VARCHAR(255), NOT NULL)
 * - name (VARCHAR(100), NOT NULL)
 * - created_at (TIMESTAMP, NOT NULL)
 * - updated_at (TIMESTAMP, NOT NULL)
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected User() {
        // JPA 기본 생성자
    }

    public User(
            UUID id,
            String email,
            String password,
            String name,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /* =========================
       비즈니스 메서드
       ========================= */

    /**
     * 프로필 이름 변경
     * dirty checking에 의해 UPDATE 쿼리 발생
     */
    public void changeName(String name) {
        this.name = name;
    }

    /* =========================
       JPA 생명주기 콜백
       ========================= */

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /* =========================
       Getter
       ========================= */

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
