package com.example.demo.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public class CalendarResponseDto {

    @Schema(description = "캘린더 ID")
    private final UUID id;

    @Schema(description = "타임존")
    private final String timezone;

    @Schema(description = "채팅 세션 ID")
    private final UUID chatSessionId;

    @Schema(description = "생성 시간")
    private final LocalDateTime createdAt;

    @Schema(description = "수정 시간")
    private final LocalDateTime updatedAt;

    public CalendarResponseDto(UUID id,
                               String timezone,
                               UUID chatSessionId,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt) {
        this.id = id;
        this.timezone = timezone;
        this.chatSessionId = chatSessionId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getTimezone() {
        return timezone;
    }

    public UUID getChatSessionId() {
        return chatSessionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}


