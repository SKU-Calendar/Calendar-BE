package com.example.demo.event.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class EventDeleteRequestDto {

    @NotNull
    private UUID eventId;

    protected EventDeleteRequestDto() {
    }

    public EventDeleteRequestDto(UUID eventId) {
        this.eventId = eventId;
    }

    public UUID getEventId() {
        return eventId;
    }
}


