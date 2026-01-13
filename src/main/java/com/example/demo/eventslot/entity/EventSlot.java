package com.example.demo.eventslot.entity;

import com.example.demo.event.entity.Event;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "event_slots")
public class EventSlot {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false, columnDefinition = "BINARY(16)")
    private Event event;

    @Column(name = "slot_start_at", nullable = false)
    private LocalDateTime slotStartAt;

    @Column(name = "slot_end_at", nullable = false)
    private LocalDateTime slotEndAt;

    @Column(name = "slot_index", nullable = false)
    private int slotIndex;

    @Column(name = "slot_title", nullable = false, length = 200)
    private String slotTitle;

    @Column(name = "is_done")
    private Boolean isDone;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected EventSlot() {
    }

    public EventSlot(UUID id,
                     Event event,
                     String slotTitle,
                     int slotIndex,
                     LocalDateTime slotStartAt,
                     LocalDateTime slotEndAt,
                     Boolean isDone) {
        this.id = id;
        this.event = event;
        this.slotTitle = slotTitle;
        this.slotIndex = slotIndex;
        this.slotStartAt = slotStartAt;
        this.slotEndAt = slotEndAt;
        this.isDone = isDone;
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

    public Event getEvent() {
        return event;
    }

    public LocalDateTime getSlotStartAt() {
        return slotStartAt;
    }

    public LocalDateTime getSlotEndAt() {
        return slotEndAt;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public String getSlotTitle() {
        return slotTitle;
    }

    public Boolean getIsDone() {
        return isDone;
    }

    public boolean isDone() {
        return Boolean.TRUE.equals(isDone);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void update(String slotTitle,
                       Integer slotIndex,
                       LocalDateTime slotStartAt,
                       LocalDateTime slotEndAt,
                       Boolean isDone) {
        if (slotTitle != null) {
            this.slotTitle = slotTitle;
        }
        if (slotIndex != null) {
            this.slotIndex = slotIndex;
        }
        if (slotStartAt != null) {
            this.slotStartAt = slotStartAt;
        }
        if (slotEndAt != null) {
            this.slotEndAt = slotEndAt;
        }
        if (isDone != null) {
            this.isDone = isDone;
        }
    }
}


