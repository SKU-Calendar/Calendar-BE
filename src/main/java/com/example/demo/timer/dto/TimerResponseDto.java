package com.example.demo.timer.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TimerResponseDto(
        UUID id,
        UUID userId,
        String status,
        LocalDateTime startAt,
        LocalDateTime lastStartedAt,
        LocalDateTime stoppedAt,
        Integer studyTime,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

