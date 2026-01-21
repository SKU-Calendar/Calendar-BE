package com.example.demo.group.service;

import com.example.demo.group.dto.*;
import java.util.List;
import java.util.UUID;

public interface GroupService {
    GroupResponse createGroup(UUID userId, GroupCreateRequest req);

    List<GroupResponse> listAll(UUID userIdOrNull);
    List<GroupResponse> listPublic(UUID userIdOrNull);
    List<GroupResponse> listMy(UUID userId);

    GroupDetailResponse getDetail(UUID userIdOrNull, UUID groupId);

    GroupResponse updateGroup(UUID userId, UUID groupId, GroupUpdateRequest req);
    void deleteGroup(UUID userId, UUID groupId);

    List<GroupMemberResponse> listMembers(UUID userIdOrNull, UUID groupId);

    void leave(UUID userId, UUID groupId);
    void kick(UUID userId, UUID groupId, UUID targetUserId);

    void joinPublic(UUID userId, UUID groupId);

    GroupInviteResponse createInvite(UUID userId, UUID groupId);
    void acceptInvite(UUID userId, InviteAcceptRequest req);
}
