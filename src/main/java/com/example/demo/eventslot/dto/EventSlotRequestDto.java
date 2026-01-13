package com.example.demo.eventslot.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class EventSlotRequestDto {

    @NotNull
    private LocalDateTime slotStartAt;

    @NotNull
    private LocalDateTime slotEndAt;

    @NotNull
    private Integer slotIndex;

    @NotNull
    private String slotTitle;

    private Boolean isDone;

    protected EventSlotRequestDto() {
    }

    public EventSlotRequestDto(LocalDateTime slotStartAt,
                               LocalDateTime slotEndAt,
                               Integer slotIndex,
                               String slotTitle,
                               Boolean isDone) {
        this.slotStartAt = slotStartAt;
        this.slotEndAt = slotEndAt;
        this.slotIndex = slotIndex;
        this.slotTitle = slotTitle;
        this.isDone = isDone;
    }

    public LocalDateTime getSlotStartAt() {
        return slotStartAt;
    }

    public LocalDateTime getSlotEndAt() {
        return slotEndAt;
    }

    public Integer getSlotIndex() {
        return slotIndex;
    }

    public String getSlotTitle() {
        return slotTitle;
    }

    public Boolean getIsDone() {
        return isDone;
    }
}


