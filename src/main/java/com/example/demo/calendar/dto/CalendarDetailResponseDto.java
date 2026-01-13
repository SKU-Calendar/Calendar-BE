package com.example.demo.calendar.dto;

import com.example.demo.event.dto.EventResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CalendarDetailResponseDto {

    private final UUID id;
    private final String timezone;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<EventResponseDto> events;

    public CalendarDetailResponseDto(UUID id,
                                     String timezone,
                                     LocalDateTime createdAt,
                                     LocalDateTime updatedAt,
                                     List<EventResponseDto> events) {
        this.id = id;
        this.timezone = timezone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.events = events;
    }

    public UUID getId() {
        return id;
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

    public List<EventResponseDto> getEvents() {
        return events;
    }
}


