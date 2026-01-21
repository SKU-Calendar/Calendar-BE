package com.example.demo.user.controller;

import com.example.demo.common.security.AuthHelper;
import com.example.demo.user.dto.UserMeUpdateRequest;
import com.example.demo.user.dto.UserMeUpdateResponse;
import com.example.demo.user.dto.UserProfileResponse;
import com.example.demo.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Users", description = "유저 프로필 API")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthHelper authHelper;

    public UserController(UserService userService, AuthHelper authHelper) {
        this.userService = userService;
        this.authHelper = authHelper;
    }

    @Operation(summary = "프로필 조회", description = "내 프로필을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me(
            @AuthenticationPrincipal String principal
    ) {
        UUID userId = authHelper.requireUserId(principal);
        return ResponseEntity.ok(userService.getMe(userId));
    }

    @Operation(summary = "상대 프로필 조회", description = "특정 유저의 프로필을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "404", description = "유저 없음")
    })
    @GetMapping("/{user_id}")
    public ResponseEntity<UserProfileResponse> profile(
            @AuthenticationPrincipal String principal,
            @PathVariable("user_id") UUID userId
    ) {
        // 로그인만 체크(권한 정책은 필요하면 여기서 확장)
        authHelper.requireUserId(principal);
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @Operation(summary = "프로필 수정", description = "내 프로필을 수정합니다. (현재는 name만 수정)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PatchMapping("/me")
    public ResponseEntity<UserMeUpdateResponse> updateMe(
            @AuthenticationPrincipal String principal,
            @Valid @RequestBody UserMeUpdateRequest req
    ) {
        UUID userId = authHelper.requireUserId(principal);
        return ResponseEntity.ok(userService.updateMe(userId, req));
    }
}
