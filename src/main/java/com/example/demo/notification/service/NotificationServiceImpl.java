package com.example.demo.notification.service;

import com.example.demo.common.exception.ApiException;
import com.example.demo.common.exception.ErrorCode;
import com.example.demo.notification.dto.NotificationResponse;
import com.example.demo.notification.entity.Notification;
import com.example.demo.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> list(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public NotificationResponse markRead(UUID userId, UUID notificationId) {
        Notification n = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "알림을 찾을 수 없습니다."));

        n.markRead(); // dirty checking
        NotificationResponse res = toResponse(n);

        // 읽음 처리도 실시간 반영하고 싶으면 push
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", res);
        return res;
    }

    @Override
    public void createAndPush(UUID targetUserId) {
        Notification n = Notification.builder()
                .id(UUID.randomUUID())
                .userId(targetUserId)
                .isRead(false)
                .readAt(null)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(n);

        NotificationResponse payload = toResponse(n);
        messagingTemplate.convertAndSendToUser(targetUserId.toString(), "/queue/notifications", payload);
    }

    @Override
    public void createAndPushMany(List<UUID> targetUserIds) {
        List<UUID> deduped = targetUserIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        for (UUID uid : deduped) {
            createAndPush(uid);
        }
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getUserId(),
                n.isRead(),
                n.getReadAt(),
                n.getCreatedAt()
        );
    }
}
