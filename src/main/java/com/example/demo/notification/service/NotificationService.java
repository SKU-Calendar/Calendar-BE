package com.example.demo.notification.service;

import com.example.demo.notification.dto.NotificationListResponse;
import com.example.demo.notification.dto.NotificationResponse;

import java.util.UUID;

public interface NotificationService {
    NotificationListResponse listByEmail(String email, int size);
    NotificationResponse markReadByEmail(String email, UUID notificationId);

    /** ✅ 외부(그룹 등) 이벤트에서 호출: 특정 userId에게 알림 생성 + WS push */
    UUID notifyUser(UUID userId);
}
