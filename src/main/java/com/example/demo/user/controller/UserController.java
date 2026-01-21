// src/main/java/com/example/demo/users/controller/UserController.java
package com.example.demo.users.controller;

import com.example.demo.users.dto.UpdateMyProfileRequest;
import com.example.demo.users.dto.UserProfileResponse;
import com.example.demo.users.service.UserService;
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

    // ✅ 프로필 조회
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me(
            @AuthenticationPrincipal String principal
    ) {
        return ResponseEntity.ok(userService.getMe(principal));
    }

    // ✅ 상대 프로필 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getOther(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(userService.getById(userId));
    }

    // ✅ 프로필 수정
    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMe(
            @AuthenticationPrincipal String principal,
            @Valid @RequestBody UpdateMyProfileRequest req
    ) {
        return ResponseEntity.ok(userService.updateMe(principal, req));
    }
}
