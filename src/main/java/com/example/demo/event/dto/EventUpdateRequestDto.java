package com.example.demo.event.dto;

import com.example.demo.eventslot.dto.EventSlotRequestDto;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class EventUpdateRequestDto {

    @NotNull
    private UUID eventId;

    private String status;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String color;

    private List<EventSlotRequestDto> slots;

    protected EventUpdateRequestDto() {
    }

    public EventUpdateRequestDto(UUID eventId,
                                 String status,
                                 LocalDateTime startAt,
                                 LocalDateTime endAt,
                                 String color,
                                 List<EventSlotRequestDto> slots) {
        this.eventId = eventId;
        this.status = status;
        this.startAt = startAt;
        this.endAt = endAt;
        this.color = color;
        this.slots = slots;
    }

    public UUID getEventId() {
        return eventId;
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

    public List<EventSlotRequestDto> getSlots() {
        return slots;
    }
}


