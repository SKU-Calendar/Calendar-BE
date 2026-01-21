package com.example.demo.notification.service;

import com.example.demo.notification.dto.NotificationReadResponse;
import com.example.demo.notification.dto.NotificationResponse;
import com.example.demo.notification.entity.Notification;
import com.example.demo.notification.entity.NotificationType;
import com.example.demo.notification.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<NotificationResponse> listMyNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public NotificationReadResponse markAsRead(UUID userId, UUID notificationId) {
        Notification n = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다."));

        n.markRead();

        return new NotificationReadResponse(n.getId(), n.isRead(), n.getReadAt());
    }

    @Override
    public void create(UUID receiverUserId, NotificationType type, String title, String body, UUID refId) {
        notificationRepository.save(new Notification(receiverUserId, type, title, body, refId));
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getType().name(),
                n.getTitle(),
                n.getBody(),
                n.getRefId(),
                n.isRead(),
                n.getReadAt(),
                n.getCreatedAt()
        );
    }
}
