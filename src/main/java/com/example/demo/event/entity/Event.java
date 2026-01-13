package com.example.demo.event.entity;

import com.example.demo.calendar.entity.Calendar;
import com.example.demo.eventslot.entity.EventSlot;
import com.example.demo.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "calendar_id", nullable = false, columnDefinition = "BINARY(16)")
    private Calendar calendar;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false, columnDefinition = "BINARY(16)")
    private User createdBy;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "color", length = 20)
    private String color;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventSlot> slots = new ArrayList<>();

    protected Event() {
    }

    public Event(UUID id,
                 Calendar calendar,
                 User createdBy,
                 String status,
                 LocalDateTime startAt,
                 LocalDateTime endAt,
                 String color) {
        this.id = id;
        this.calendar = calendar;
        this.createdBy = createdBy;
        this.status = status;
        this.startAt = startAt;
        this.endAt = endAt;
        this.color = color;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getColor() {
        return color;
    }

    public List<EventSlot> getSlots() {
        return slots;
    }

    public void update(String status,
                       LocalDateTime startAt,
                       LocalDateTime endAt,
                       String color) {
        if (status != null) {
            this.status = status;
        }
        if (startAt != null) {
            this.startAt = startAt;
        }
        if (endAt != null) {
            this.endAt = endAt;
        }
        if (color != null) {
            this.color = color;
        }
    }
}


