package com.example.demo.eventslot.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventSlotResponseDto {

    private final UUID id;
    private final LocalDateTime slotStartAt;
    private final LocalDateTime slotEndAt;
    private final int slotIndex;
    private final String slotTitle;
    private final Boolean isDone;

    public EventSlotResponseDto(UUID id,
                                LocalDateTime slotStartAt,
                                LocalDateTime slotEndAt,
                                int slotIndex,
                                String slotTitle,
                                Boolean isDone) {
        this.id = id;
        this.slotStartAt = slotStartAt;
        this.slotEndAt = slotEndAt;
        this.slotIndex = slotIndex;
        this.slotTitle = slotTitle;
        this.isDone = isDone;
    }

    public UUID getId() {
        return id;
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
}


