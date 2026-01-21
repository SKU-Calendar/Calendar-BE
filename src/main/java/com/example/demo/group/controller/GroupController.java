package com.example.demo.group.controller;

import com.example.demo.common.exception.ApiException;
import com.example.demo.common.exception.ErrorCode;
import com.example.demo.group.dto.*;
import com.example.demo.group.service.GroupService;
import com.example.demo.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Group", description = "그룹 관리 API")
@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final UserRepository userRepository;

    private UUID requireUserId(String email) {
        if (email == null || email.isBlank()) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }
        return userRepository.findByEmail(email)
                .map(u -> u.getId())
                .orElseThrow(() ->
                        new ApiException(ErrorCode.UNAUTHORIZED, "사용자 정보를 찾을 수 없습니다."));
    }

    // ================= 그룹 =================

    @Operation(
            summary = "그룹 생성",
            description = "새 그룹을 생성하고 생성자를 OWNER로 등록합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "그룹 생성 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email,
            @Valid @RequestBody GroupCreateRequest req
    ) {
        UUID userId = requireUserId(email);
        return ResponseEntity.ok(groupService.createGroup(userId, req));
    }

    @Operation(
            summary = "그룹 목록 조회",
            description = "전체 그룹 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping
    public ResponseEntity<List<GroupResponse>> listAllGroups(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email
    ) {
        UUID userId = requireUserId(email);
        return ResponseEntity.ok(groupService.listAllGroups(userId));
    }

    @Operation(
            summary = "공개 그룹 조회",
            description = "공개(isPublic=true) 그룹만 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/public")
    public ResponseEntity<List<GroupResponse>> listPublicGroups(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email
    ) {
        UUID userId = requireUserId(email);
        return ResponseEntity.ok(groupService.listPublicGroups(userId));
    }

    @Operation(
            summary = "내 그룹 조회",
            description = "내가 속한 그룹 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/me")
    public ResponseEntity<List<GroupResponse>> listMyGroups(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email
    ) {
        UUID userId = requireUserId(email);
        return ResponseEntity.ok(groupService.listMyGroups(userId));
    }

    @Operation(
            summary = "그룹 상세 조회",
            description = "그룹 상세 정보 및 내 역할을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "그룹 없음")
    })
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> getGroupDetail(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email,
            @Parameter(description = "그룹 ID", required = true)
            @PathVariable UUID groupId
    ) {
        UUID userId = requireUserId(email);
        return ResponseEntity.ok(groupService.getGroupDetail(userId, groupId));
    }

    @Operation(
            summary = "그룹 정보 수정",
            description = "그룹 이름/공개 여부를 수정합니다. (OWNER만 가능)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "OWNER 아님")
    })
    @PatchMapping("/{groupId}")
    public ResponseEntity<GroupResponse> updateGroup(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email,
            @PathVariable UUID groupId,
            @RequestBody GroupUpdateRequest req
    ) {
        UUID userId = requireUserId(email);
        return ResponseEntity.ok(groupService.updateGroup(userId, groupId, req));
    }

    @Operation(
            summary = "그룹 삭제",
            description = "그룹을 삭제합니다. (OWNER만 가능)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "OWNER 아님")
    })
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email,
            @PathVariable UUID groupId
    ) {
        UUID userId = requireUserId(email);
        groupService.deleteGroup(userId, groupId);
        return ResponseEntity.noContent().build();
    }

    // ================= 멤버 =================

    @Operation(
            summary = "그룹 멤버 목록 조회",
            description = "그룹 멤버 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "그룹 멤버 아님")
    })
    @GetMapping("/{groupId}/member")
    public ResponseEntity<List<GroupMemberResponse>> listMembers(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email,
            @PathVariable UUID groupId
    ) {
        UUID userId = requireUserId(email);
        return ResponseEntity.ok(groupService.listMembers(userId, groupId));
    }

    @Operation(
            summary = "그룹 탈퇴",
            description = "본인이 그룹에서 탈퇴합니다. (OWNER 불가)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "탈퇴 성공"),
            @ApiResponse(responseCode = "400", description = "OWNER 탈퇴 불가")
    })
    @DeleteMapping("/{groupId}/me")
    public ResponseEntity<Void> leaveGroup(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email,
            @PathVariable UUID groupId
    ) {
        UUID userId = requireUserId(email);
        groupService.leaveGroup(userId, groupId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "그룹 멤버 강퇴",
            description = "그룹 멤버를 강퇴합니다. (OWNER만 가능)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "강퇴 성공"),
            @ApiResponse(responseCode = "403", description = "OWNER 아님")
    })
    @DeleteMapping("/{groupId}/member")
    public ResponseEntity<Void> kickMember(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email,
            @PathVariable UUID groupId,
            @Valid @RequestBody KickMemberRequest req
    ) {
        UUID ownerUserId = requireUserId(email);
        groupService.kickMember(ownerUserId, groupId, req.userId());
        return ResponseEntity.noContent().build();
    }

    // ================= 가입 / 초대 =================

    @Operation(
            summary = "공개 그룹 가입",
            description = "공개 그룹에 코드 없이 가입합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가입 성공"),
            @ApiResponse(responseCode = "403", description = "비공개 그룹")
    })
    @PostMapping("/{groupId}/join")
    public ResponseEntity<Void> joinPublicGroup(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email,
            @PathVariable UUID groupId
    ) {
        UUID userId = requireUserId(email);
        groupService.joinPublicGroup(userId, groupId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "그룹 초대 코드 생성",
            description = "그룹 초대 코드를 생성합니다. (OWNER만 가능)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "403", description = "OWNER 아님")
    })
    @PostMapping("/{groupId}/invite")
    public ResponseEntity<InviteCreateResponse> createInvite(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email,
            @PathVariable UUID groupId
    ) {
        UUID ownerUserId = requireUserId(email);
        return ResponseEntity.ok(groupService.createInvite(ownerUserId, groupId));
    }

    @Operation(
            summary = "초대 코드로 그룹 가입",
            description = "초대 코드를 사용해 그룹에 가입합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가입 성공"),
            @ApiResponse(responseCode = "400", description = "만료/유효하지 않은 코드")
    })
    @PostMapping("/invite/accept")
    public ResponseEntity<Void> acceptInvite(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email,
            @Valid @RequestBody InviteAcceptRequest req
    ) {
        UUID userId = requireUserId(email);
        groupService.acceptInvite(userId, req.inviteCode());
        return ResponseEntity.ok().build();
    }
}
