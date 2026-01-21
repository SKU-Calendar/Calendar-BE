package com.example.demo.notification.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String type,
        String title,
        String body,
        UUID refId,
        boolean isRead,
        Instant readAt,
        Instant createdAt
) {}
