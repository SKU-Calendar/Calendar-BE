package com.example.demo.notification.hook;

import com.example.demo.group.repository.GroupMemberRepository;
import com.example.demo.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GroupNotificationHookImpl implements GroupNotificationHook {

    private final NotificationService notificationService;
    private final GroupMemberRepository groupMemberRepository;

    /**
     * "내용은 프론트가 한다" 조건이므로
     * 서버는 그냥 '알림이 생김'만 쌓는다.
     *
     * ✅ 정책(최소/안전):
     * - JOIN: 기존 멤버들에게 알림 (본인은 제외)
     * - KICK: 강퇴당한 본인에게 알림
     * - INVITE_CREATED: 굳이 안 쌓음(원하면 나중에 추가)
     */
    @Override
    public void onGroupJoined(UUID groupId, UUID joinedUserId) {
        List<UUID> memberIds = groupMemberRepository.findByGroupIdOrderByJoinedAtAsc(groupId)
                .stream()
                .map(m -> m.getUserId())
                .filter(uid -> !uid.equals(joinedUserId))
                .toList();

        notificationService.createAndPushMany(memberIds);
    }

    @Override
    public void onGroupKicked(UUID groupId, UUID kickedUserId) {
        notificationService.createAndPush(kickedUserId);
    }
}
