package com.example.demo.calendar.entity;

import com.example.demo.event.entity.Event;
import com.example.demo.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "calendars")
public class Calendar {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false, columnDefinition = "BINARY(16)")
    private User owner;

    @Column(name = "timezone", nullable = false, length = 50)
    private String timezone;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events = new ArrayList<>();

    protected Calendar() {
    }

    public Calendar(UUID id, User owner, String timezone) {
        this.id = id;
        this.owner = owner;
        this.timezone = timezone;
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

    public User getOwner() {
        return owner;
    }

    public String getTimezone() {
        return timezone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void update(String timezone) {
        if (timezone != null) {
            this.timezone = timezone;
        }
    }
}


