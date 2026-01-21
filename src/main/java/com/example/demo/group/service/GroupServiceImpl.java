package com.example.demo.group.service;

import com.example.demo.group.dto.*;
import com.example.demo.group.entity.*;
import com.example.demo.group.event.GroupJoinedEvent;
import com.example.demo.group.exception.GroupException;
import com.example.demo.group.repository.GroupInviteRepository;
import com.example.demo.group.repository.GroupMemberRepository;
import com.example.demo.group.repository.GroupRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupInviteRepository groupInviteRepository;
    private final ApplicationEventPublisher publisher; // ✅ 추가

    private static final SecureRandom random = new SecureRandom();
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // 헷갈리는 문자 제거

    public GroupServiceImpl(
            GroupRepository groupRepository,
            GroupMemberRepository groupMemberRepository,
            GroupInviteRepository groupInviteRepository,
            ApplicationEventPublisher publisher // ✅ 추가
    ) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupInviteRepository = groupInviteRepository;
        this.publisher = publisher;
    }

    @Override
    public GroupResponse createGroup(UUID userId, GroupCreateRequest req) {
        Group group = new Group(userId, req.groupName(), req.isPublic());
        Group saved = groupRepository.save(group);

        // 만든 사람은 OWNER로 멤버십 생성
        groupMemberRepository.save(new GroupMember(saved.getId(), userId, GroupRole.OWNER));

        return toGroupResponse(userId, saved);
    }

    @Override
    public List<GroupResponse> listAll(UUID userIdOrNull) {
        return groupRepository.findAll().stream()
                .sorted(Comparator.comparing(Group::getCreatedAt).reversed())
                .map(g -> toGroupResponse(userIdOrNull, g))
                .toList();
    }

    @Override
    public List<GroupResponse> listPublic(UUID userIdOrNull) {
        return groupRepository.findByIsPublicTrueOrderByCreatedAtDesc().stream()
                .map(g -> toGroupResponse(userIdOrNull, g))
                .toList();
    }

    @Override
    public List<GroupResponse> listMy(UUID userId) {
        List<GroupMember> memberships = groupMemberRepository.findByUserIdOrderByJoinedAtDesc(userId);
        if (memberships.isEmpty()) return List.of();

        List<UUID> groupIds = memberships.stream().map(GroupMember::getGroupId).toList();

        Map<UUID, Group> groupMap = groupRepository.findAllById(groupIds).stream()
                .collect(HashMap::new, (m, g) -> m.put(g.getId(), g), HashMap::putAll);

        List<GroupResponse> out = new ArrayList<>();
        for (GroupMember m : memberships) {
            Group g = groupMap.get(m.getGroupId());
            if (g != null) out.add(toGroupResponse(userId, g));
        }
        return out;
    }

    @Override
    public GroupDetailResponse getDetail(UUID userIdOrNull, UUID groupId) {
        Group g = groupRepository.findById(groupId)
                .orElseThrow(() -> GroupException.notFound("그룹을 찾을 수 없습니다."));

        long memberCount = groupMemberRepository.countByGroupId(groupId);
        String myRole = resolveMyRole(userIdOrNull, groupId);

        return new GroupDetailResponse(
                g.getId(),
                g.getGroupName(),
                g.isPublic(),
                g.getOwnerUserId(),
                memberCount,
                myRole,
                g.getCreatedAt(),
                g.getUpdatedAt()
        );
    }

    @Override
    public GroupResponse updateGroup(UUID userId, UUID groupId, GroupUpdateRequest req) {
        Group g = groupRepository.findById(groupId)
                .orElseThrow(() -> GroupException.notFound("그룹을 찾을 수 없습니다."));

        requireOwner(userId, groupId);

        g.update(req.groupName(), req.isPublic());
        return toGroupResponse(userId, g);
    }

    @Override
    public void deleteGroup(UUID userId, UUID groupId) {
        Group g = groupRepository.findById(groupId)
                .orElseThrow(() -> GroupException.notFound("그룹을 찾을 수 없습니다."));

        requireOwner(userId, groupId);

        // 안전하게 직접 정리(프로젝트 FK 정책에 따라 CASCADE면 생략 가능)
        groupInviteRepository.findAll().stream()
                .filter(inv -> inv.getGroupId().equals(groupId))
                .forEach(inv -> groupInviteRepository.deleteById(inv.getId()));

        groupMemberRepository.findByGroupIdOrderByJoinedAtAsc(groupId)
                .forEach(m -> groupMemberRepository.deleteById(m.getId()));

        groupRepository.delete(g);
    }

    @Override
    public List<GroupMemberResponse> listMembers(UUID userIdOrNull, UUID groupId) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> GroupException.notFound("그룹을 찾을 수 없습니다."));

        return groupMemberRepository.findByGroupIdOrderByJoinedAtAsc(groupId).stream()
                .map(m -> new GroupMemberResponse(
                        m.getUserId(),
                        m.getRole().name(),
                        m.getStatus().name(),
                        m.getJoinedAt()
                ))
                .toList();
    }

    @Override
    public void leave(UUID userId, UUID groupId) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> GroupException.notFound("그룹을 찾을 수 없습니다."));

        GroupMember me = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> GroupException.badRequest("이미 그룹에 속해있지 않습니다."));

        if (me.isOwner()) {
            throw GroupException.badRequest("OWNER는 탈퇴할 수 없습니다. (소유권 위임 후 탈퇴 정책 필요)");
        }

        groupMemberRepository.deleteByGroupIdAndUserId(groupId, userId);
    }

    @Override
    public void kick(UUID userId, UUID groupId, UUID targetUserId) {
        requireOwner(userId, groupId);

        GroupMember target = groupMemberRepository.findByGroupIdAndUserId(groupId, targetUserId)
                .orElseThrow(() -> GroupException.notFound("강퇴 대상 멤버를 찾을 수 없습니다."));

        if (target.isOwner()) {
            throw GroupException.badRequest("OWNER는 강퇴할 수 없습니다.");
        }

        groupMemberRepository.deleteByGroupIdAndUserId(groupId, targetUserId);
    }

    /**
     * ✅ 공개 그룹은 코드 없이 가입 가능
     * - 가입 성공 시 GroupJoinedEvent 발행
     */
    @Override
    public void joinPublic(UUID userId, UUID groupId) {
        Group g = groupRepository.findById(groupId)
                .orElseThrow(() -> GroupException.notFound("그룹을 찾을 수 없습니다."));

        if (!g.isPublic()) {
            throw GroupException.forbidden("비공개 그룹은 초대코드로만 가입할 수 있습니다.");
        }

        boolean exists = groupMemberRepository.findByGroupIdAndUserId(groupId, userId).isPresent();
        if (exists) throw GroupException.conflict("이미 가입한 그룹입니다.");

        groupMemberRepository.save(new GroupMember(groupId, userId, GroupRole.MEMBER));

        // ✅ 알림(다음 단계)로 연결될 이벤트 훅
        publisher.publishEvent(new GroupJoinedEvent(
                groupId,
                g.getOwnerUserId(),
                userId,
                GroupJoinedEvent.JoinType.PUBLIC
        ));
    }

    /**
     * ✅ 초대코드로 가입
     * - 가입 성공 시 GroupJoinedEvent 발행
     */
    @Override
    public void acceptInvite(UUID userId, InviteAcceptRequest req) {
        GroupInvite invite = groupInviteRepository.findByInviteCode(req.inviteCode())
                .orElseThrow(() -> GroupException.notFound("초대코드를 찾을 수 없습니다."));

        if (invite.isExpired()) {
            throw GroupException.badRequest("만료된 초대코드입니다.");
        }

        UUID groupId = invite.getGroupId();

        boolean exists = groupMemberRepository.findByGroupIdAndUserId(groupId, userId).isPresent();
        if (exists) throw GroupException.conflict("이미 가입한 그룹입니다.");

        groupMemberRepository.save(new GroupMember(groupId, userId, GroupRole.MEMBER));

        Group g = groupRepository.findById(groupId)
                .orElseThrow(() -> GroupException.notFound("그룹을 찾을 수 없습니다."));

        // ✅ 알림(다음 단계)로 연결될 이벤트 훅
        publisher.publishEvent(new GroupJoinedEvent(
                groupId,
                g.getOwnerUserId(),
                userId,
                GroupJoinedEvent.JoinType.INVITE_CODE
        ));
    }

    @Override
    public GroupInviteResponse createInvite(UUID userId, UUID groupId) {
        requireOwner(userId, groupId);

        Instant expiresAt = Instant.now().plus(7, ChronoUnit.DAYS); // 7일 만료(원하면 변경)
        String code = generateUniqueInviteCode();

        GroupInvite invite = groupInviteRepository.save(new GroupInvite(groupId, code, userId, expiresAt));
        return new GroupInviteResponse(invite.getId(), invite.getGroupId(), invite.getInviteCode(), invite.getExpiresAt(), invite.getCreatedAt());
    }

    // ----------------- helpers -----------------

    private void requireOwner(UUID userId, UUID groupId) {
        GroupMember me = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> GroupException.forbidden("그룹 멤버가 아닙니다."));

        if (me.getRole() != GroupRole.OWNER) {
            throw GroupException.forbidden("OWNER 권한이 필요합니다.");
        }
    }

    private GroupResponse toGroupResponse(UUID userIdOrNull, Group g) {
        long memberCount = groupMemberRepository.countByGroupId(g.getId());
        String myRole = resolveMyRole(userIdOrNull, g.getId());
        return new GroupResponse(
                g.getId(),
                g.getGroupName(),
                g.isPublic(),
                g.getOwnerUserId(),
                memberCount,
                myRole,
                g.getCreatedAt(),
                g.getUpdatedAt()
        );
    }

    private String resolveMyRole(UUID userIdOrNull, UUID groupId) {
        if (userIdOrNull == null) return "NONE";
        return groupMemberRepository.findByGroupIdAndUserId(groupId, userIdOrNull)
                .map(m -> m.getRole().name())
                .orElse("NONE");
    }

    private String generateUniqueInviteCode() {
        for (int i = 0; i < 10; i++) {
            String code = randomCode(10);
            boolean exists = groupInviteRepository.findByInviteCode(code).isPresent();
            if (!exists) return code;
        }
        throw GroupException.conflict("초대코드 생성에 실패했습니다. 다시 시도해주세요.");
    }

    private String randomCode(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }
}
