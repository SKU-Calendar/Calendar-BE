package com.example.demo.eventslot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "슬롯 수정 요청 DTO")
public class EventSlotUpdateRequestDto {

    @Schema(description = "슬롯 시작 시간", example = "2024-01-01T10:00:00")
    private LocalDateTime slotStartAt;

    @Schema(description = "슬롯 종료 시간", example = "2024-01-01T11:00:00")
    private LocalDateTime slotEndAt;

    @Schema(description = "슬롯 제목", example = "회의 준비", maxLength = 200)
    private String slotTitle;

    @Schema(description = "완료 여부", example = "true")
    @JsonProperty("isDone")
    private Boolean isDone;

    protected EventSlotUpdateRequestDto() {
    }

    public LocalDateTime getSlotStartAt() {
        return slotStartAt;
    }

    public LocalDateTime getSlotEndAt() {
        return slotEndAt;
    }

    public String getSlotTitle() {
        return slotTitle;
    }

    @JsonProperty("isDone")
    public Boolean getIsDone() {
        return isDone;
    }

    public void setSlotStartAt(LocalDateTime slotStartAt) {
        this.slotStartAt = slotStartAt;
    }

    public void setSlotEndAt(LocalDateTime slotEndAt) {
        this.slotEndAt = slotEndAt;
    }

    public void setSlotTitle(String slotTitle) {
        this.slotTitle = slotTitle;
    }

    @JsonProperty("isDone")
    public void setIsDone(Boolean isDone) {
        this.isDone = isDone;
    }
}

