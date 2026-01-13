package com.example.demo.eventslot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "이벤트 ID를 포함한 슬롯 응답 DTO")
public class EventSlotWithEventIdResponseDto {

    @Schema(description = "슬롯 ID")
    private final UUID id;

    @Schema(description = "이벤트 ID")
    private final UUID eventId;

    @Schema(description = "슬롯 시작 시간")
    private final LocalDateTime slotStartAt;

    @Schema(description = "슬롯 종료 시간")
    private final LocalDateTime slotEndAt;

    @Schema(description = "슬롯 인덱스")
    private final int slotIndex;

    @Schema(description = "슬롯 제목")
    private final String slotTitle;

    @Schema(description = "완료 여부")
    @JsonProperty("isDone")
    private final Boolean isDone;

    public EventSlotWithEventIdResponseDto(UUID id,
                                            UUID eventId,
                                            LocalDateTime slotStartAt,
                                            LocalDateTime slotEndAt,
                                            int slotIndex,
                                            String slotTitle,
                                            Boolean isDone) {
        this.id = id;
        this.eventId = eventId;
        this.slotStartAt = slotStartAt;
        this.slotEndAt = slotEndAt;
        this.slotIndex = slotIndex;
        this.slotTitle = slotTitle;
        this.isDone = isDone;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
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

    @JsonProperty("isDone")
    public Boolean getIsDone() {
        return isDone;
    }

    @JsonProperty("isDone")
    public boolean isDone() {
        return Boolean.TRUE.equals(isDone);
    }
}

