package com.example.demo.group.service;

import com.example.demo.group.dto.*;

import java.util.List;
import java.util.UUID;

public interface GroupService {
    GroupResponse createGroup(UUID userId, GroupCreateRequest req);

    List<GroupResponse> listAllGroups(UUID userId);        // /api/group
    List<GroupResponse> listPublicGroups(UUID userId);     // /api/group/public
    List<GroupResponse> listMyGroups(UUID userId);         // /api/group/me

    GroupResponse getGroupDetail(UUID userId, UUID groupId);

    GroupResponse updateGroup(UUID userId, UUID groupId, GroupUpdateRequest req);

    void deleteGroup(UUID userId, UUID groupId);

    List<GroupMemberResponse> listMembers(UUID userId, UUID groupId);

    void leaveGroup(UUID userId, UUID groupId);

    void kickMember(UUID ownerUserId, UUID groupId, UUID targetUserId);

    void joinPublicGroup(UUID userId, UUID groupId);

    InviteCreateResponse createInvite(UUID ownerUserId, UUID groupId);

    void acceptInvite(UUID userId, String inviteCode);
}
