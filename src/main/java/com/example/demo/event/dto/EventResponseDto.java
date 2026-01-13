package com.example.demo.event.dto;

import com.example.demo.eventslot.dto.EventSlotResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class EventResponseDto {

    private final UUID id;
    private final String status;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final String color;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<EventSlotResponseDto> slots;

    public EventResponseDto(UUID id,
                            String status,
                            LocalDateTime startAt,
                            LocalDateTime endAt,
                            String color,
                            LocalDateTime createdAt,
                            LocalDateTime updatedAt,
                            List<EventSlotResponseDto> slots) {
        this.id = id;
        this.status = status;
        this.startAt = startAt;
        this.endAt = endAt;
        this.color = color;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.slots = slots;
    }

    public UUID getId() {
        return id;
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

    public String getColor() {
        return color;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<EventSlotResponseDto> getSlots() {
        return slots;
    }
}


