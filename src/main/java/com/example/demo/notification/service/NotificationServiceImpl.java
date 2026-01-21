package com.example.demo.notification.service;

import com.example.demo.notification.dto.*;
import com.example.demo.notification.entity.Notification;
import com.example.demo.notification.repository.NotificationRepository;
import com.example.demo.notification.ws.NotificationWebSocketHandler;
import com.example.demo.notification.ws.NotificationWsMessage;
import com.example.demo.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationWebSocketHandler wsHandler;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository,
                                   NotificationWebSocketHandler wsHandler) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.wsHandler = wsHandler;
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationListResponse listByEmail(String email, int size) {
        UUID userId = getUserIdByEmail(email);

        int safeSize = Math.max(1, Math.min(size, 200));
        var pageable = PageRequest.of(0, safeSize);

        List<Notification> entities = notificationRepository.findMyNotifications(userId, pageable);
        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(userId);

        List<NotificationResponse> items = entities.stream().map(this::toResponse).toList();
        return new NotificationListResponse(unreadCount, items);
    }

    @Override
    @Transactional
    public NotificationResponse markReadByEmail(String email, UUID notificationId) {
        UUID userId = getUserIdByEmail(email);

        Notification n = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다."));

        n.markRead(LocalDateTime.now());

        NotificationResponse res = toResponse(n);

        // ✅ 읽음 처리도 실시간 동기화
        wsHandler.pushToUser(userId, new NotificationWsMessage(
                "NOTIFICATION_READ",
                n.getId(),
                n.isRead(),
                n.getCreatedAt(),
                n.getReadAt()
        ));

        return res;
    }

    @Override
    @Transactional
    public UUID notifyUser(UUID userId) {
        LocalDateTime now = LocalDateTime.now();
        Notification saved = notificationRepository.save(Notification.create(userId, now));

        // ✅ 새 알림 생성 push
        wsHandler.pushToUser(userId, new NotificationWsMessage(
                "NOTIFICATION_CREATED",
                saved.getId(),
                saved.isRead(),
                saved.getCreatedAt(),
                saved.getReadAt()
        ));

        return saved.getId();
    }

    private UUID getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(u -> u.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."));
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(n.getId(), n.isRead(), n.getReadAt(), n.getCreatedAt());
    }
}
