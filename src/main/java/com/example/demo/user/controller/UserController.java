package com.example.demo.user.controller;

import com.example.demo.user.dto.UpdateMyProfileRequest;
import com.example.demo.user.dto.UserProfileResponse;
import com.example.demo.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ✅ 내 프로필
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me(
            @AuthenticationPrincipal String principal
    ) {
        return ResponseEntity.ok(userService.getMe(principal));
    }

    // ✅ 유저 프로필 조회(공개)
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getById(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(userService.getById(userId));
    }

    // ✅ 내 프로필 수정(이름)
    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMe(
            @AuthenticationPrincipal String principal,
            @Valid @RequestBody UpdateMyProfileRequest req
    ) {
        return ResponseEntity.ok(userService.updateMe(principal, req));
    }
}
