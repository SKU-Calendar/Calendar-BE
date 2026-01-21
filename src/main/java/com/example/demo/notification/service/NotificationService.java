package com.example.demo.notification.service;

import com.example.demo.notification.dto.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    List<NotificationResponse> list(UUID userId);
    NotificationResponse markRead(UUID userId, UUID notificationId);

    // 그룹 이벤트용: 알림 생성 + 실시간 push
    void createAndPush(UUID targetUserId);
    void createAndPushMany(List<UUID> targetUserIds);
}
