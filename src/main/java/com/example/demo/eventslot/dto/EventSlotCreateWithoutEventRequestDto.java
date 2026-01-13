package com.example.demo.eventslot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "이벤트 없이 슬롯 생성 요청 DTO")
public class EventSlotCreateWithoutEventRequestDto {

    @Schema(description = "캘린더 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "calendarId는 필수입니다")
    private UUID calendarId;

    @Schema(description = "슬롯 시작 시간", required = true, example = "2026-01-12T10:00:00")
    @NotNull(message = "slotStartAt은 필수입니다")
    private LocalDateTime slotStartAt;

    @Schema(description = "슬롯 종료 시간", required = true, example = "2026-01-12T11:00:00")
    @NotNull(message = "slotEndAt은 필수입니다")
    private LocalDateTime slotEndAt;

    @Schema(description = "슬롯 인덱스", example = "0")
    @NotNull(message = "slotIndex는 필수입니다")
    private Integer slotIndex;

    @Schema(description = "슬롯 제목", example = "집중 작업", maxLength = 200)
    @NotNull(message = "slotTitle은 필수입니다")
    private String slotTitle;

    protected EventSlotCreateWithoutEventRequestDto() {
    }

    public UUID getCalendarId() {
        return calendarId;
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

    public void setCalendarId(UUID calendarId) {
        this.calendarId = calendarId;
    }

    public void setSlotStartAt(LocalDateTime slotStartAt) {
        this.slotStartAt = slotStartAt;
    }

    public void setSlotEndAt(LocalDateTime slotEndAt) {
        this.slotEndAt = slotEndAt;
    }

    public void setSlotIndex(Integer slotIndex) {
        this.slotIndex = slotIndex;
    }

    public void setSlotTitle(String slotTitle) {
        this.slotTitle = slotTitle;
    }
}

