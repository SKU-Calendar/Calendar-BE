package com.example.demo.notification.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Notification() {}

    private Notification(UUID id, UUID userId, boolean isRead, LocalDateTime readAt, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.isRead = isRead;
        this.readAt = readAt;
        this.createdAt = createdAt;
    }

    /** ✅ 새 알림 생성 (ERD 변경 없이 최소 알림) */
    public static Notification create(UUID userId, LocalDateTime now) {
        return new Notification(UUID.randomUUID(), userId, false, null, now);
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public boolean isRead() { return isRead; }
    public LocalDateTime getReadAt() { return readAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void markRead(LocalDateTime now) {
        if (!this.isRead) {
            this.isRead = true;
            this.readAt = now;
        }
    }
}
