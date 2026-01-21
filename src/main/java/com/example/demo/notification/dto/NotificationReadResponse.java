package com.example.demo.notification.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationReadResponse(
        UUID id,
        boolean isRead,
        Instant readAt
) {}
