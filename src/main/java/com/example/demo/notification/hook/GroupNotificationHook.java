package com.example.demo.notification.hook;

import java.util.UUID;

public interface GroupNotificationHook {
    // 나중에 notification + websocket 붙일 때 여기 구현체에서 처리
    default void onGroupJoined(UUID groupId, UUID joinedUserId) {}
    default void onGroupKicked(UUID groupId, UUID kickedUserId) {}
    default void onInviteCreated(UUID groupId, String inviteCode) {}
}
