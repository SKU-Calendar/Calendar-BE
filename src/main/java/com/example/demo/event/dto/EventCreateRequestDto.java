package com.example.demo.event.dto;

import com.example.demo.eventslot.dto.EventSlotRequestDto;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class EventCreateRequestDto {

    @NotNull(message = "status는 필수입니다")
    private String status;

    @NotNull
    private LocalDateTime startAt;

    @NotNull
    private LocalDateTime endAt;

    private String color;

    private List<EventSlotRequestDto> slots;

    protected EventCreateRequestDto() {
    }

    public EventCreateRequestDto(String status,
                                 LocalDateTime startAt,
                                 LocalDateTime endAt,
                                 String color,
                                 List<EventSlotRequestDto> slots) {
        this.status = status;
        this.startAt = startAt;
        this.endAt = endAt;
        this.color = color;
        this.slots = slots;
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


