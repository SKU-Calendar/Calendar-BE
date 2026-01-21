package com.example.demo.group.service;

import com.example.demo.group.dto.*;
import com.example.demo.group.entity.*;
import com.example.demo.group.repository.*;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final GroupInviteRepository inviteRepository;

    // ✅ email -> userId 변환을 위해 추가
    private final UserRepository userRepository;

    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final SecureRandom random = new SecureRandom();

    public GroupService(GroupRepository groupRepository,
                        GroupMemberRepository memberRepository,
                        GroupInviteRepository inviteRepository,
                        UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.inviteRepository = inviteRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // ✅ Wrappers: email 기반 API (Controller가 이걸 호출)
    // =========================================================

    private UUID resolveUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유저를 찾을 수 없습니다."));
        return user.getId();
    }

    @Transactional
    public GroupResponse createGroupByEmail(String email, GroupCreateRequest req) {
        return createGroup(resolveUserIdByEmail(email), req);
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> listMyGroupsByEmail(String email) {
        return listMyGroups(resolveUserIdByEmail(email));
    }

    @Transactional(readOnly = true)
    public GroupResponse getGroupByEmail(String email, UUID groupId) {
        return getGroup(resolveUserIdByEmail(email), groupId);
    }

    @Transactional
    public GroupResponse updateGroupByEmail(String email, UUID groupId, GroupUpdateRequest req) {
        return updateGroup(resolveUserIdByEmail(email), groupId, req);
    }

    @Transactional
    public void deleteGroupByEmail(String email, UUID groupId) {
        deleteGroup(resolveUserIdByEmail(email), groupId);
    }

    @Transactional
    public GroupInviteResponse issueInviteByEmail(String email, UUID groupId) {
        return issueInvite(resolveUserIdByEmail(email), groupId);
    }

    @Transactional
    public void acceptInviteByEmail(String email, String inviteCode) {
        acceptInvite(resolveUserIdByEmail(email), inviteCode);
    }

    @Transactional(readOnly = true)
    public List<GroupMemberResponse> listMembersByEmail(String email, UUID groupId) {
        return listMembers(resolveUserIdByEmail(email), groupId);
    }

    @Transactional
    public void kickMemberByEmail(String email, UUID groupId, UUID targetUserId) {
        kickMember(resolveUserIdByEmail(email), groupId, targetUserId);
    }

    @Transactional
    public void leaveGroupByEmail(String email, UUID groupId) {
        leaveGroup(resolveUserIdByEmail(email), groupId);
    }

    // =========================================================
    // ✅ 추가: 공개/전체 그룹 리스트
    // =========================================================

    // ✅ 공개 그룹 전체(탐색용)
    @Transactional(readOnly = true)
    public List<GroupResponse> listPublicGroups() {
        return groupRepository.findByIsPublicTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ✅ (선택) 진짜 전체 그룹(all) - 개발/관리자용
    @Transactional(readOnly = true)
    public List<GroupResponse> listAllGroups() {
        return groupRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // 기존 로직 (UUID userId 기반) - 그대로 유지
    // =========================================================

    // -----------------------------
    // Group
    // -----------------------------

    @Transactional
    public GroupResponse createGroup(UUID ownerUserId, GroupCreateRequest req) {
        Instant now = Instant.now();
        UUID groupId = UUID.randomUUID();

        Group group = new Group(groupId, ownerUserId, req.groupName(), req.isPublic(), now);
        groupRepository.save(group);

        // ✅ OWNER 멤버십 자동 생성
        GroupMember owner = new GroupMember(UUID.randomUUID(), groupId, ownerUserId, GroupMemberRole.OWNER, now);
        memberRepository.save(owner);

        return toResponse(group);
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> listMyGroups(UUID userId) {
        // ✅ “내가 속한 그룹” 기준
        List<GroupMember> myMemberships = memberRepository.findByUserId(userId);
        if (myMemberships.isEmpty()) return List.of();

        List<UUID> groupIds = myMemberships.stream().map(GroupMember::getGroupId).distinct().toList();
        return groupRepository.findByIdIn(groupIds).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public GroupResponse getGroup(UUID requesterId, UUID groupId) {
        requireMember(requesterId, groupId);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));
        return toResponse(group);
    }

    @Transactional
    public GroupResponse updateGroup(UUID requesterId, UUID groupId, GroupUpdateRequest req) {
        requireOwner(requesterId, groupId);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

        group.update(req.groupName(), req.isPublic(), Instant.now());
        return toResponse(group);
    }

    @Transactional
    public void deleteGroup(UUID requesterId, UUID groupId) {
        requireOwner(requesterId, groupId);

        // 멤버십/초대코드 정리 (초대코드는 데이터 보존 정책에 따라 삭제/유지 선택)
        memberRepository.deleteByGroupId(groupId);
        groupRepository.deleteById(groupId);
    }

    // -----------------------------
    // Invite
    // -----------------------------

    @Transactional
    public GroupInviteResponse issueInvite(UUID requesterId, UUID groupId) {
        requireOwner(requesterId, groupId);

        String code = generateUniqueCode(8);
        Instant now = Instant.now();
        Instant expiresAt = now.plus(7, ChronoUnit.DAYS);

        GroupInvite invite = new GroupInvite(
                UUID.randomUUID(),
                groupId,
                code,
                requesterId,
                now,
                expiresAt
        );

        inviteRepository.save(invite);
        return new GroupInviteResponse(groupId, code, expiresAt);
    }

    @Transactional
    public void acceptInvite(UUID requesterId, String inviteCode) {
        GroupInvite invite = inviteRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 코드입니다."));

        if (invite.getExpiresAt() != null && invite.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalStateException("만료된 초대 코드입니다.");
        }

        UUID groupId = invite.getGroupId();

        // 그룹 존재 체크(삭제된 그룹의 초대코드 방지)
        groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

        // 이미 멤버면 아무 작업 안 함(원하면 예외로 바꿔도 됨)
        if (memberRepository.existsByGroupIdAndUserId(groupId, requesterId)) {
            return;
        }

        memberRepository.save(new GroupMember(
                UUID.randomUUID(),
                groupId,
                requesterId,
                GroupMemberRole.MEMBER,
                Instant.now()
        ));
    }

    // -----------------------------
    // Members
    // -----------------------------

    @Transactional(readOnly = true)
    public List<GroupMemberResponse> listMembers(UUID requesterId, UUID groupId) {
        requireMember(requesterId, groupId);

        return memberRepository.findByGroupId(groupId).stream()
                .map(m -> new GroupMemberResponse(m.getUserId(), m.getRole(), m.getJoinedAt()))
                .toList();
    }

    @Transactional
    public void kickMember(UUID requesterId, UUID groupId, UUID targetUserId) {
        requireOwner(requesterId, groupId);

        // owner는 강퇴 불가
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));
        if (group.getOwnerUserId().equals(targetUserId)) {
            throw new IllegalStateException("그룹 소유자는 제거할 수 없습니다.");
        }

        // 멤버인지 확인 후 삭제
        if (!memberRepository.existsByGroupIdAndUserId(groupId, targetUserId)) {
            throw new IllegalArgumentException("해당 사용자는 그룹 멤버가 아닙니다.");
        }

        memberRepository.deleteByGroupIdAndUserId(groupId, targetUserId);
    }

    @Transactional
    public void leaveGroup(UUID requesterId, UUID groupId) {
        requireMember(requesterId, groupId);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));
        if (group.getOwnerUserId().equals(requesterId)) {
            throw new IllegalStateException("OWNER는 나가기를 할 수 없습니다. (권한 위임/그룹 삭제 정책 필요)");
        }

        memberRepository.deleteByGroupIdAndUserId(groupId, requesterId);
    }

    // -----------------------------
    // Guards
    // -----------------------------

    private void requireMember(UUID userId, UUID groupId) {
        if (!memberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new IllegalStateException("그룹 멤버만 접근할 수 있습니다.");
        }
    }

    private void requireOwner(UUID userId, UUID groupId) {
        boolean isOwner = memberRepository
                .findByGroupIdAndUserIdAndRole(groupId, userId, GroupMemberRole.OWNER)
                .isPresent();

        if (!isOwner) {
            throw new IllegalStateException("그룹 OWNER만 수행할 수 있습니다.");
        }
    }

    private GroupResponse toResponse(Group g) {
        return new GroupResponse(
                g.getId(),
                g.getOwnerUserId(),
                g.getGroupName(),
                g.isPublic(),
                g.getCreatedAt(),
                g.getUpdatedAt()
        );
    }

    private String generateUniqueCode(int length) {
        for (int attempt = 0; attempt < 30; attempt++) {
            String code = randomCode(length);
            if (!inviteRepository.existsByInviteCode(code)) return code;
        }
        throw new IllegalStateException("초대 코드 생성에 실패했습니다. 다시 시도해주세요.");
    }

    private String randomCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }
}
