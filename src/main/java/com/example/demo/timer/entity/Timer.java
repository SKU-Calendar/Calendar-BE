package com.example.demo.timer.entity;

import com.example.demo.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 타이머 정보를 나타내는 엔티티.
 *
 * timer 테이블 구조:
 * - id (UUID, PK)
 * - user_id (UUID, NOT NULL, FK)
 * - status (VARCHAR(20), NOT NULL) -- RUNNING / PAUSED / STOPPED
 * - start_at (TIMESTAMP, NOT NULL)
 * - last_started_at (TIMESTAMP, NULL)
 * - stopped_at (TIMESTAMP, NULL)
 * - study_time (INT, NOT NULL, DEFAULT 0)
 * - created_at (TIMESTAMP, NOT NULL)
 * - updated_at (TIMESTAMP, NOT NULL)
 */
@Entity
@Table(name = "timer")
public class Timer {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private User user;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TimerStatus status;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "last_started_at")
    private LocalDateTime lastStartedAt;

    @Column(name = "stopped_at")
    private LocalDateTime stoppedAt;

    @Column(name = "study_time", nullable = false)
    private Integer studyTime;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Timer() {
        // JPA 기본 생성자
    }

    public Timer(
            UUID id,
            User user,
            TimerStatus status,
            LocalDateTime startAt,
            LocalDateTime lastStartedAt,
            LocalDateTime stoppedAt,
            Integer studyTime
    ) {
        this.id = id;
        this.user = user;
        this.status = status;
        this.startAt = startAt;
        this.lastStartedAt = lastStartedAt;
        this.stoppedAt = stoppedAt;
        this.studyTime = studyTime;
    }

    /* =========================
       비즈니스 메서드
       ========================= */

    /**
     * study_time에 초 단위 시간을 누적
     */
    public void accumulateStudyTime(int seconds) {
        this.studyTime += seconds;
    }

    /**
     * status를 PAUSED로 변경하고 lastStartedAt을 null로 설정
     */
    public void pause() {
        this.status = TimerStatus.PAUSED;
        this.lastStartedAt = null;
    }

    /**
     * status를 RUNNING으로 변경하고 lastStartedAt을 현재 시간으로 설정
     */
    public void resume(LocalDateTime now) {
        this.status = TimerStatus.RUNNING;
        this.lastStartedAt = now;
    }

    /**
     * status를 STOPPED로 변경하고 stoppedAt을 현재 시간으로, lastStartedAt을 null로 설정
     */
    public void stop(LocalDateTime now) {
        this.status = TimerStatus.STOPPED;
        this.stoppedAt = now;
        this.lastStartedAt = null;
    }

    /* =========================
       JPA 생명주기 콜백
       ========================= */

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.studyTime == null) {
            this.studyTime = 0;
        }
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

    public User getUser() {
        return user;
    }

    public TimerStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getLastStartedAt() {
        return lastStartedAt;
    }

    public LocalDateTime getStoppedAt() {
        return stoppedAt;
    }

    public Integer getStudyTime() {
        return studyTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

