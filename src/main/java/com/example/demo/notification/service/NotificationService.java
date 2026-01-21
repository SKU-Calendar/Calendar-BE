package com.example.demo.notification.service;

import com.example.demo.notification.dto.NotificationReadResponse;
import com.example.demo.notification.dto.NotificationResponse;
import com.example.demo.notification.entity.NotificationType;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    List<NotificationResponse> listMyNotifications(UUID userId);

    NotificationReadResponse markAsRead(UUID userId, UUID notificationId);

    // 이벤트 리스너에서 호출할 생성 메서드
    void create(UUID receiverUserId, NotificationType type, String title, String body, UUID refId);
}
