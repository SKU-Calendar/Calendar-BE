package com.example.demo.notification.controller;

import com.example.demo.notification.dto.NotificationListResponse;
import com.example.demo.notification.dto.NotificationResponse;
import com.example.demo.notification.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    private String requireEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return email;
    }

    @GetMapping
    public ResponseEntity<NotificationListResponse> list(
            @AuthenticationPrincipal String email,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(notificationService.listByEmail(requireEmail(email), size));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> read(
            @AuthenticationPrincipal String email,
            @PathVariable UUID notificationId
    ) {
        return ResponseEntity.ok(notificationService.markReadByEmail(requireEmail(email), notificationId));
    }
}
