package com.example.demo.group.controller;

import com.example.demo.common.security.CustomPrincipal;
import com.example.demo.group.dto.*;
import com.example.demo.group.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/group")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    // 그룹 생성
    @PostMapping
    public ResponseEntity<GroupResponse> create(
            @AuthenticationPrincipal CustomPrincipal principal,
            @Valid @RequestBody GroupCreateRequest req
    ) {
        return ResponseEntity.ok(groupService.createGroup(principal.userId(), req));
    }

    // 내 그룹 목록(멤버십 기반)
    @GetMapping
    public ResponseEntity<List<GroupResponse>> listMyGroups(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        return ResponseEntity.ok(groupService.listMyGroups(principal.userId()));
    }

    // 그룹 상세(멤버만)
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> get(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable UUID groupId
    ) {
        return ResponseEntity.ok(groupService.getGroup(principal.userId(), groupId));
    }

    // 그룹 수정(OWNER)
    @PatchMapping("/{groupId}")
    public ResponseEntity<GroupResponse> update(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable UUID groupId,
            @Valid @RequestBody GroupUpdateRequest req
    ) {
        return ResponseEntity.ok(groupService.updateGroup(principal.userId(), groupId, req));
    }

    // 그룹 삭제(OWNER)
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable UUID groupId
    ) {
        groupService.deleteGroup(principal.userId(), groupId);
        return ResponseEntity.noContent().build();
    }

    // 초대 코드 발급(OWNER)
    @PostMapping("/{groupId}/invite")
    public ResponseEntity<GroupInviteResponse> issueInvite(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable UUID groupId
    ) {
        return ResponseEntity.ok(groupService.issueInvite(principal.userId(), groupId));
    }

    // 초대 코드 수락(가입)
    @PostMapping("/invite/accept")
    public ResponseEntity<Void> acceptInvite(
            @AuthenticationPrincipal CustomPrincipal principal,
            @Valid @RequestBody InviteAcceptRequest req
    ) {
        groupService.acceptInvite(principal.userId(), req.inviteCode());
        return ResponseEntity.ok().build();
    }

    // 멤버 목록(멤버만)
    @GetMapping("/{groupId}/member")
    public ResponseEntity<List<GroupMemberResponse>> members(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable UUID groupId
    ) {
        return ResponseEntity.ok(groupService.listMembers(principal.userId(), groupId));
    }

    // 강퇴(OWNER)
    @DeleteMapping("/{groupId}/member/{userId}")
    public ResponseEntity<Void> kick(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable UUID groupId,
            @PathVariable UUID userId
    ) {
        groupService.kickMember(principal.userId(), groupId, userId);
        return ResponseEntity.noContent().build();
    }

    // 나가기(본인)
    @DeleteMapping("/{groupId}/me")
    public ResponseEntity<Void> leave(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable UUID groupId
    ) {
        groupService.leaveGroup(principal.userId(), groupId);
        return ResponseEntity.noContent().build();
    }
}
