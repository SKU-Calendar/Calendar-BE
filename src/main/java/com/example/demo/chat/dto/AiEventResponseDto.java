package com.example.demo.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class AiEventResponseDto {

    private String title;
    private String description;

    @JsonProperty("startAt")
    private LocalDateTime startAt;

    @JsonProperty("endAt")
    private LocalDateTime endAt;

    private List<AiSlotDto> slots;

    protected AiEventResponseDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    public List<AiSlotDto> getSlots() {
        return slots;
    }

    public void setSlots(List<AiSlotDto> slots) {
        this.slots = slots;
    }

    public static class AiSlotDto {
        @JsonProperty("slotTitle")
        private String slotTitle;

        @JsonProperty("slotNote")
        private String slotNote;

        @JsonProperty("slotStartAt")
        private LocalDateTime slotStartAt;

        @JsonProperty("slotEndAt")
        private LocalDateTime slotEndAt;

        protected AiSlotDto() {
        }

        public String getSlotTitle() {
            return slotTitle;
        }

        public void setSlotTitle(String slotTitle) {
            this.slotTitle = slotTitle;
        }

        public String getSlotNote() {
            return slotNote;
        }

        public void setSlotNote(String slotNote) {
            this.slotNote = slotNote;
        }

        public LocalDateTime getSlotStartAt() {
            return slotStartAt;
        }

        public void setSlotStartAt(LocalDateTime slotStartAt) {
            this.slotStartAt = slotStartAt;
        }

        public LocalDateTime getSlotEndAt() {
            return slotEndAt;
        }

        public void setSlotEndAt(LocalDateTime slotEndAt) {
            this.slotEndAt = slotEndAt;
        }
    }
}

