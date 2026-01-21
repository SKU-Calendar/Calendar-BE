package com.example.demo.notification.controller;

import com.example.demo.common.security.CustomPrincipal;
import com.example.demo.notification.dto.NotificationReadResponse;
import com.example.demo.notification.dto.NotificationResponse;
import com.example.demo.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Tag(name = "Notification", description = "알림 API")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    private UUID requireUserId(CustomPrincipal principal) {
        if (principal == null || principal.userId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return principal.userId();
    }

    @Operation(summary = "알림 목록 조회", description = "내 알림 목록을 최신순으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> list(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        UUID userId = requireUserId(principal);
        return ResponseEntity.ok(notificationService.listMyNotifications(userId));
    }

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "404", description = "알림 없음")
    })
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationReadResponse> markRead(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable UUID notificationId
    ) {
        UUID userId = requireUserId(principal);
        return ResponseEntity.ok(notificationService.markAsRead(userId, notificationId));
    }
}
