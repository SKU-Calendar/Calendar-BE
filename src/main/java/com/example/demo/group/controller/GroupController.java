package com.example.demo.group.controller;

import com.example.demo.group.dto.*;
import com.example.demo.group.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/group")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    private String requireEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return email;
    }

    // =========================================================
    // Group
    // =========================================================

    // 그룹 생성
    @PostMapping
    public ResponseEntity<GroupResponse> create(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody GroupCreateRequest req
    ) {
        return ResponseEntity.ok(groupService.createGroupByEmail(requireEmail(email), req));
    }

    // ✅ 내 그룹 목록(멤버십 기반) - 기존 그대로
    @GetMapping
    public ResponseEntity<List<GroupResponse>> listMyGroups(
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(groupService.listMyGroupsByEmail(requireEmail(email)));
    }

    // ✅ 공개 그룹 전체 목록(탐색용) - 추가
    // 로그인 없이도 가능하게 하고 싶으면 @AuthenticationPrincipal / requireEmail 빼면 됨
    @GetMapping("/public")
    public ResponseEntity<List<GroupResponse>> listPublicGroups() {
        return ResponseEntity.ok(groupService.listPublicGroups());
    }

    // ✅ (선택) 진짜 전체 그룹(all) - 개발/관리자용
    // 운영에서는 막는 걸 추천 (Security에서 권한 제한)
    @GetMapping("/all")
    public ResponseEntity<List<GroupResponse>> listAllGroups() {
        return ResponseEntity.ok(groupService.listAllGroups());
    }

    // 그룹 상세(멤버만)
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> get(
            @AuthenticationPrincipal String email,
            @PathVariable UUID groupId
    ) {
        return ResponseEntity.ok(groupService.getGroupByEmail(requireEmail(email), groupId));
    }

    // 그룹 수정(OWNER)
    @PatchMapping("/{groupId}")
    public ResponseEntity<GroupResponse> update(
            @AuthenticationPrincipal String email,
            @PathVariable UUID groupId,
            @Valid @RequestBody GroupUpdateRequest req
    ) {
        return ResponseEntity.ok(groupService.updateGroupByEmail(requireEmail(email), groupId, req));
    }

    // 그룹 삭제(OWNER)
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal String email,
            @PathVariable UUID groupId
    ) {
        groupService.deleteGroupByEmail(requireEmail(email), groupId);
        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // Invite
    // =========================================================

    // 초대 코드 발급(OWNER)
    @PostMapping("/{groupId}/invite")
    public ResponseEntity<GroupInviteResponse> issueInvite(
            @AuthenticationPrincipal String email,
            @PathVariable UUID groupId
    ) {
        return ResponseEntity.ok(groupService.issueInviteByEmail(requireEmail(email), groupId));
    }

    // 초대 코드 수락(가입)
    @PostMapping("/invite/accept")
    public ResponseEntity<Void> acceptInvite(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody InviteAcceptRequest req
    ) {
        groupService.acceptInviteByEmail(requireEmail(email), req.inviteCode());
        return ResponseEntity.ok().build();
    }

    // =========================================================
    // Members
    // =========================================================

    // 멤버 목록(멤버만)
    @GetMapping("/{groupId}/member")
    public ResponseEntity<List<GroupMemberResponse>> members(
            @AuthenticationPrincipal String email,
            @PathVariable UUID groupId
    ) {
        return ResponseEntity.ok(groupService.listMembersByEmail(requireEmail(email), groupId));
    }

    // 강퇴(OWNER)
    @DeleteMapping("/{groupId}/member/{userId}")
    public ResponseEntity<Void> kick(
            @AuthenticationPrincipal String email,
            @PathVariable UUID groupId,
            @PathVariable UUID userId
    ) {
        groupService.kickMemberByEmail(requireEmail(email), groupId, userId);
        return ResponseEntity.noContent().build();
    }

    // 나가기(본인)
    @DeleteMapping("/{groupId}/me")
    public ResponseEntity<Void> leave(
            @AuthenticationPrincipal String email,
            @PathVariable UUID groupId
    ) {
        groupService.leaveGroupByEmail(requireEmail(email), groupId);
        return ResponseEntity.noContent().build();
    }
}
