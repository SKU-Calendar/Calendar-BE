package com.example.demo.notification.ws;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationWsMessage(
        String type,                 // NOTIFICATION_CREATED / NOTIFICATION_READ
        UUID notificationId,
        Boolean isRead,
        LocalDateTime createdAt,
        LocalDateTime readAt
) {}
