package com.example.demo.group.service;

import com.example.demo.common.exception.ApiException;
import com.example.demo.common.exception.ErrorCode;
import com.example.demo.group.dto.*;
import com.example.demo.group.entity.Group;
import com.example.demo.group.entity.GroupInvite;
import com.example.demo.group.entity.GroupMember;
import com.example.demo.group.repository.GroupInviteRepository;
import com.example.demo.group.repository.GroupMemberRepository;
import com.example.demo.group.repository.GroupRepository;
import com.example.demo.notification.hook.GroupNotificationHook;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupInviteRepository groupInviteRepository;
    private final GroupNotificationHook notificationHook;

    private static final SecureRandom random = new SecureRandom();
    private static final String INVITE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // 헷갈리는 문자 제외

    @Override
    public GroupResponse createGroup(UUID userId, GroupCreateRequest req) {
        UUID groupId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Group group = Group.builder()
                .id(groupId)
                .ownerUserId(userId)
                .groupName(req.groupName())
                .isPublic(req.isPublic() != null && req.isPublic())
                .createdAt(now)
                .updatedAt(now)
                .build();

        groupRepository.save(group);

        GroupMember ownerMember = GroupMember.builder()
                .id(UUID.randomUUID())
                .groupId(groupId)
                .userId(userId)
                .role("OWNER")
                .joinedAt(now)
                .build();

        groupMemberRepository.save(ownerMember);

        return toResponse(group, 1, "OWNER");
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupResponse> listAllGroups(UUID userId) {
        List<Group> groups = groupRepository.findAll();
        return groups.stream()
                .sorted(Comparator.comparing(Group::getCreatedAt).reversed())
                .map(g -> {
                    long cnt = groupMemberRepository.countByGroupId(g.getId());
                    String myRole = groupMemberRepository.findByGroupIdAndUserId(g.getId(), userId)
                            .map(GroupMember::getRole).orElse(null);
                    return toResponse(g, cnt, myRole);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupResponse> listPublicGroups(UUID userId) {
        return groupRepository.findByIsPublicTrueOrderByCreatedAtDesc().stream()
                .map(g -> {
                    long cnt = groupMemberRepository.countByGroupId(g.getId());
                    String myRole = groupMemberRepository.findByGroupIdAndUserId(g.getId(), userId)
                            .map(GroupMember::getRole).orElse(null);
                    return toResponse(g, cnt, myRole);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupResponse> listMyGroups(UUID userId) {
        List<GroupMember> memberships = groupMemberRepository.findByUserIdOrderByJoinedAtDesc(userId);
        if (memberships.isEmpty()) return List.of();

        // 그룹 조회 최적화: id 목록으로 한 번에 가져오기
        List<UUID> groupIds = memberships.stream().map(GroupMember::getGroupId).distinct().toList();
        Map<UUID, Group> groupMap = groupRepository.findAllById(groupIds).stream()
                .collect(Collectors.toMap(Group::getId, g -> g));

        Map<UUID, String> roleMap = memberships.stream()
                .collect(Collectors.toMap(GroupMember::getGroupId, GroupMember::getRole, (a,b)->a));

        return groupIds.stream()
                .map(id -> groupMap.get(id))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Group::getCreatedAt).reversed())
                .map(g -> {
                    long cnt = groupMemberRepository.countByGroupId(g.getId());
                    return toResponse(g, cnt, roleMap.get(g.getId()));
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GroupResponse getGroupDetail(UUID userId, UUID groupId) {
        Group group = getGroupOrThrow(groupId);
        long cnt = groupMemberRepository.countByGroupId(groupId);
        String myRole = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .map(GroupMember::getRole).orElse(null);
        return toResponse(group, cnt, myRole);
    }

    @Override
    public GroupResponse updateGroup(UUID userId, UUID groupId, GroupUpdateRequest req) {
        Group group = getGroupOrThrow(groupId);
        requireOwner(userId, groupId);

        group.update(req.groupName(), req.isPublic());
        // JPA dirty checking
        long cnt = groupMemberRepository.countByGroupId(groupId);
        return toResponse(group, cnt, "OWNER");
    }

    @Override
    public void deleteGroup(UUID userId, UUID groupId) {
        Group group = getGroupOrThrow(groupId);
        requireOwner(userId, groupId);

        // 관련 데이터 정리
        groupInviteRepository.deleteByGroupId(groupId);
        groupMemberRepository.deleteByGroupId(groupId);
        groupRepository.delete(group);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupMemberResponse> listMembers(UUID userId, UUID groupId) {
        // 멤버 조회는 멤버만 가능하도록(원하면 공개그룹은 모두 허용도 가능)
        requireMember(userId, groupId);

        return groupMemberRepository.findByGroupIdOrderByJoinedAtAsc(groupId).stream()
                .map(m -> new GroupMemberResponse(m.getUserId(), m.getRole(), m.getJoinedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public void leaveGroup(UUID userId, UUID groupId) {
        requireMember(userId, groupId);

        Group group = getGroupOrThrow(groupId);
        GroupMember me = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "멤버가 아닙니다."));

        if (me.isOwner()) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "OWNER는 탈퇴할 수 없습니다. (소유권 위임 기능 필요)");
        }

        groupMemberRepository.deleteByGroupIdAndUserId(groupId, userId);
    }

    @Override
    public void kickMember(UUID ownerUserId, UUID groupId, UUID targetUserId) {
        requireOwner(ownerUserId, groupId);

        if (ownerUserId.equals(targetUserId)) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "OWNER 본인은 강퇴할 수 없습니다.");
        }

        GroupMember target = groupMemberRepository.findByGroupIdAndUserId(groupId, targetUserId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "강퇴 대상이 멤버가 아닙니다."));

        if (target.isOwner()) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "OWNER는 강퇴할 수 없습니다.");
        }

        groupMemberRepository.deleteByGroupIdAndUserId(groupId, targetUserId);
        notificationHook.onGroupKicked(groupId, targetUserId);
    }

    @Override
    public void joinPublicGroup(UUID userId, UUID groupId) {
        Group group = getGroupOrThrow(groupId);
        if (!group.isPublic()) {
            throw new ApiException(ErrorCode.FORBIDDEN, "비공개 그룹은 초대 코드로만 가입할 수 있습니다.");
        }
        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new ApiException(ErrorCode.CONFLICT, "이미 가입된 그룹입니다.");
        }

        groupMemberRepository.save(GroupMember.builder()
                .id(UUID.randomUUID())
                .groupId(groupId)
                .userId(userId)
                .role("MEMBER")
                .joinedAt(LocalDateTime.now())
                .build());

        notificationHook.onGroupJoined(groupId, userId);
    }

    @Override
    public InviteCreateResponse createInvite(UUID ownerUserId, UUID groupId) {
        requireOwner(ownerUserId, groupId);

        String code = generateInviteCode(8);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusDays(7); // 기본 7일 (원하면 변경)

        GroupInvite invite = GroupInvite.builder()
                .id(UUID.randomUUID())
                .groupId(groupId)
                .inviteCode(code)
                .createdBy(ownerUserId)
                .createdAt(now)
                .expiresAt(expiresAt)
                .build();

        // code 충돌 극히 낮지만, unique 위반 대비 재시도
        for (int i = 0; i < 3; i++) {
            try {
                groupInviteRepository.save(invite);
                notificationHook.onInviteCreated(groupId, code);
                return new InviteCreateResponse(code, expiresAt);
            } catch (Exception e) {
                code = generateInviteCode(8);
                invite = GroupInvite.builder()
                        .id(invite.getId())
                        .groupId(groupId)
                        .inviteCode(code)
                        .createdBy(ownerUserId)
                        .createdAt(now)
                        .expiresAt(expiresAt)
                        .build();
            }
        }
        throw new ApiException(ErrorCode.CONFLICT, "초대 코드 생성에 실패했습니다. 다시 시도해주세요.");
    }

    @Override
    public void acceptInvite(UUID userId, String inviteCode) {
        GroupInvite invite = groupInviteRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "유효하지 않은 초대 코드입니다."));

        if (invite.isExpired()) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "만료된 초대 코드입니다.");
        }

        UUID groupId = invite.getGroupId();
        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new ApiException(ErrorCode.CONFLICT, "이미 가입된 그룹입니다.");
        }

        groupMemberRepository.save(GroupMember.builder()
                .id(UUID.randomUUID())
                .groupId(groupId)
                .userId(userId)
                .role("MEMBER")
                .joinedAt(LocalDateTime.now())
                .build());

        notificationHook.onGroupJoined(groupId, userId);
    }

    // ----------------- private helpers -----------------

    private Group getGroupOrThrow(UUID groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "그룹을 찾을 수 없습니다."));
    }

    private void requireMember(UUID userId, UUID groupId) {
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new ApiException(ErrorCode.FORBIDDEN, "그룹 멤버만 접근할 수 있습니다.");
        }
    }

    private void requireOwner(UUID userId, UUID groupId) {
        GroupMember gm = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ApiException(ErrorCode.FORBIDDEN, "그룹 멤버가 아닙니다."));
        if (!gm.isOwner()) throw new ApiException(ErrorCode.FORBIDDEN, "OWNER만 가능합니다.");
    }

    private static String generateInviteCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(INVITE_CHARS.charAt(random.nextInt(INVITE_CHARS.length())));
        }
        return sb.toString();
    }

    private static GroupResponse toResponse(Group g, long memberCount, String myRole) {
        return new GroupResponse(
                g.getId(),
                g.getOwnerUserId(),
                g.getGroupName(),
                g.isPublic(),
                g.getCreatedAt(),
                g.getUpdatedAt(),
                memberCount,
                myRole
        );
    }
}
