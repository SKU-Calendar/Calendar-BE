package com.example.demo.notification.listener;

import com.example.demo.group.event.GroupJoinedEvent;
import com.example.demo.notification.entity.NotificationType;
import com.example.demo.notification.service.NotificationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
public class NotificationEventListener {

    private final NotificationService notificationService;

    public NotificationEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * ✅ 커밋 성공 이후에만 알림을 쌓는다(중요)
     * - 그룹 가입이 DB에 반영되었을 때만 알림 생성
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onGroupJoined(GroupJoinedEvent event) {

        NotificationType type = (event.joinType() == GroupJoinedEvent.JoinType.PUBLIC)
                ? NotificationType.GROUP_JOINED_PUBLIC
                : NotificationType.GROUP_JOINED_INVITE;

        String title = "그룹 가입 알림";
        String body = (event.joinType() == GroupJoinedEvent.JoinType.PUBLIC)
                ? "새 멤버가 공개 그룹에 가입했습니다."
                : "새 멤버가 초대코드로 그룹에 가입했습니다.";

        // ✅ 받는 사람 = 그룹 OWNER
        notificationService.create(
                event.ownerUserId(),
                type,
                title,
                body,
                event.groupId()
        );

        // (다음 단계) 여기서 WS push도 같이 하면 됨
    }
}
