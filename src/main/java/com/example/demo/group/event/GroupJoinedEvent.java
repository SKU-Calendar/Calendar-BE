package com.example.demo.group.event;

import java.util.UUID;

/**
 * 그룹 가입 이벤트
 * - PUBLIC : 공개그룹 코드없이 가입 (/api/group/{groupId}/join)
 * - INVITE_CODE : 초대코드로 가입 (/api/group/invite/accept)
 *
 * Notification 도메인에서 이 이벤트를 구독(@TransactionalEventListener)해서
 * DB에 notification을 쌓고, WS로 push 하면 됨.
 */
public record GroupJoinedEvent(
        UUID groupId,
        UUID ownerUserId,
        UUID joinedUserId,
        JoinType joinType
) {
    public enum JoinType {
        PUBLIC,
        INVITE_CODE
    }
}
