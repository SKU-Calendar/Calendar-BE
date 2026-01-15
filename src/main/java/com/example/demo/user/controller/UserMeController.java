package com.example.demo.user.controller;

import com.example.demo.common.security.CustomPrincipal;
import com.example.demo.user.dto.ProfileResponse;
import com.example.demo.user.dto.ProfileUpdateRequest;
import com.example.demo.user.service.UserMeService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserMeController {

    private final UserMeService userMeService;

    public UserMeController(UserMeService userMeService) {
        this.userMeService = userMeService;
    }

    @GetMapping("/me")
    public ProfileResponse me(@AuthenticationPrincipal CustomPrincipal principal) {
        return userMeService.getMe(principal.userId());
    }

    @PatchMapping("/me")
    public ProfileResponse updateMe(
            @AuthenticationPrincipal CustomPrincipal principal,
            @Valid @RequestBody ProfileUpdateRequest req
    ) {
        return userMeService.updateMe(principal.userId(), req);
    }
}
