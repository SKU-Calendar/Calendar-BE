package com.example.demo.notification.hook;

import java.util.UUID;

public interface GroupNotificationHook {
    default void onGroupJoined(UUID groupId, UUID joinedUserId) {}
    default void onGroupKicked(UUID groupId, UUID kickedUserId) {}
    default void onInviteCreated(UUID groupId, String inviteCode) {}
}
