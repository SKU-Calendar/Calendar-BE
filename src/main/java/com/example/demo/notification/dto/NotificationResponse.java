package com.example.demo.notification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID userId,
        boolean isRead,
        LocalDateTime readAt,
        LocalDateTime createdAt
) { }
