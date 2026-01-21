package com.example.demo.group.controller;

import com.example.demo.common.security.CustomPrincipal;
import com.example.demo.group.dto.*;
import com.example.demo.group.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Group", description = "그룹/멤버/초대 API")
@RestController
@RequestMapping("/api/group")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    private UUID requireUserId(CustomPrincipal principal) {
        if (principal == null || principal.userId() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."
            );
        }
        return principal.userId();
    }

    // 그룹 생성
    @Operation(summary = "그룹 생성", description = "그룹을 생성하고 생성자를 OWNER로 멤버 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping
    public ResponseEntity<GroupResponse> create(
            @AuthenticationPrincipal CustomPrincipal principal,
            @Valid @RequestBody GroupCreateRequest req
    ) {
        UUID userId = requireUserId(principal);
        return ResponseEntity.ok(groupService.createGroup(userId, req));
    }

    // 그룹 목록 조회 (전체)
    @Operation(summary = "그룹 목록 조회(전체)", description = "전체 그룹 목록을 반환합니다. 로그인 시 myRole이 포함됩니다.")
    @GetMapping
    public ResponseEntity<List<GroupResponse>> listAll(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        UUID userIdOrNull = (principal == null ? null : principal.userId());
        return ResponseEntity.ok(groupService.listAll(userIdOrNull));
    }

    // 공개 그룹 조회
    @Operation(summary = "공개 그룹 조회", description = "is_public=true 그룹만 반환합니다. 로그인 시 myRole이 포함됩니다.")
    @GetMapping("/public")
    public ResponseEntity<List<GroupResponse>> listPublic(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        UUID userIdOrNull = (principal == null ? null : principal.userId());
        return ResponseEntity.ok(groupService.listPublic(userIdOrNull));
    }

    // 내 그룹 조회
    @Operation(summary = "내 그룹 조회", description = "내가 속한 그룹(멤버십 기반)만 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping("/me")
    public ResponseEntity<List<GroupResponse>> listMy(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        UUID userId = requireUserId(principal);
        return ResponseEntity.ok(groupService.listMy(userId));
    }

    // 그룹 상세
    @Operation(summary = "그룹 상세 조회", description = "그룹 상세 + memberCount + myRole을 반환합니다.")
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDetailResponse> detail(
            @AuthenticationPrincipal CustomPrincipal principal,
            @Parameter(description = "그룹 ID") @PathVariable UUID groupId
    ) {
        UUID userIdOrNull = (principal == null ? null : principal.userId());
        return ResponseEntity.ok(groupService.getDetail(userIdOrNull, groupId));
    }

    // 그룹 수정 (OWNER)
    @Operation(summary = "그룹 정보 수정(OWNER)", description = "OWNER만 그룹명/공개여부를 수정할 수 있습니다.")
    @PatchMapping("/{groupId}")
    public ResponseEntity<GroupResponse> update(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable UUID groupId,
            @RequestBody GroupUpdateRequest req
    ) {
        UUID userId = requireUserId(principal);
        return ResponseEntity.ok(groupService.updateGroup(userId, groupId, req));
    }

    // 그룹 삭제 (OWNER)
    @Operation(summary = "그룹 삭제(OWNER)", description = "OWNER만 그룹을 삭제할 수 있습니다.")
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable UUID groupId
    ) {
        UUID userId = requireUserId(principal);
        groupService.deleteGroup(userId, groupId);
        return ResponseEntity.ok().build();
    }

    // 멤버 목록 조회
    @Operation(summary = "그룹 멤버 목록 조회", description = "그룹 멤버 목록을 조회합니다.")
    @GetMapping("/{groupId}/member")
    public ResponseEntity<List<GroupMemberResponse>> members(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable UUID groupId
    ) {
        UUID userIdOrNull = (principal == null ? null : principal.userId());
        return ResponseEntity.ok(groupService.listMembers(userIdOrNull, groupId));
    }

    // 그룹 탈퇴 (본인)
    @Operation(summary = "그룹 탈퇴(본인)", description = "본인이 그룹에서 탈퇴합니다. OWNER는 탈퇴 불가(정책).")
    @DeleteMapping("/{groupId}/me")
    public ResponseEntity<Void> leave(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable UUID groupId
    ) {
        UUID userId = requireUserId(principal);
        groupService.leave(userId, groupId);
        return ResponseEntity.ok().build();
    }

    // 그룹 강퇴 (OWNER) - 스크린샷 방식 유지: DELETE /{groupId}/member + body(userId)
    @Operation(summary = "그룹 강퇴(OWNER)", description = "OWNER가 특정 유저를 그룹에서 강퇴합니다.")
    @DeleteMapping("/{groupId}/member")
    public ResponseEntity<Void> kick(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable UUID groupId,
            @Valid @RequestBody GroupKickRequest req
    ) {
        UUID userId = requireUserId(principal);
        groupService.kick(userId, groupId, req.userId());
        return ResponseEntity.ok().build();
    }

    // 공개그룹 코드 없이 가입
    @Operation(summary = "공개그룹 코드 없이 가입", description = "is_public=true인 그룹은 초대코드 없이 가입 가능합니다.")
    @PostMapping("/{groupId}/join")
    public ResponseEntity<Void> joinPublic(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable UUID groupId
    ) {
        UUID userId = requireUserId(principal);
        groupService.joinPublic(userId, groupId);
        return ResponseEntity.ok().build();
    }

    // 초대코드 생성 (OWNER)
    @Operation(summary = "그룹 초대 코드 생성(OWNER)", description = "OWNER만 초대코드를 생성할 수 있습니다.")
    @PostMapping("/{groupId}/invite")
    public ResponseEntity<GroupInviteResponse> createInvite(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable UUID groupId
    ) {
        UUID userId = requireUserId(principal);
        return ResponseEntity.ok(groupService.createInvite(userId, groupId));
    }

    // 초대코드로 가입
    @Operation(summary = "그룹 초대 코드로 가입", description = "초대코드(inviteCode)로 그룹에 가입합니다.")
    @PostMapping("/invite/accept")
    public ResponseEntity<Void> acceptInvite(
            @AuthenticationPrincipal CustomPrincipal principal,
            @Valid @RequestBody InviteAcceptRequest req
    ) {
        UUID userId = requireUserId(principal);
        groupService.acceptInvite(userId, req);
        return ResponseEntity.ok().build();
    }
}
