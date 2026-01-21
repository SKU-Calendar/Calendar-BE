package com.example.demo.user.controller;

import com.example.demo.user.dto.UpdateMyProfileRequest;
import com.example.demo.user.dto.UserProfileResponse;
import com.example.demo.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "User", description = "유저/프로필 관련 API")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ================= 내 정보 =================

    @Operation(
            summary = "내 프로필 조회",
            description = "로그인한 사용자의 프로필 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String principal
    ) {
        return ResponseEntity.ok(userService.getMe(principal));
    }

    @Operation(
            summary = "내 프로필 수정",
            description = "로그인한 사용자의 프로필 정보를 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMe(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String principal,
            @Valid @RequestBody UpdateMyProfileRequest req
    ) {
        return ResponseEntity.ok(userService.updateMe(principal, req));
    }

    // ================= 공개 프로필 =================

    @Operation(
            summary = "유저 프로필 조회",
            description = "특정 사용자의 공개 프로필을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "유저 없음")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getById(
            @Parameter(description = "유저 ID(UUID)", required = true)
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(userService.getById(userId));
    }
}
