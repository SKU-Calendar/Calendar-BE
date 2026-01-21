package com.example.demo.notification.controller;

import com.example.demo.notification.dto.NotificationResponse;
import com.example.demo.notification.service.NotificationService;
import com.example.demo.notification.support.NotificationAuthResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Notification", description = "알림 API")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationAuthResolver authResolver;

    @Operation(
            summary = "알림 목록 조회",
            description = "내 알림 목록을 최신순으로 조회합니다. (A안: principal(email) 기반)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> list(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String principal
    ) {
        UUID userId = authResolver.resolveUserId(principal, null);
        return ResponseEntity.ok(notificationService.list(userId));
    }

    @Operation(
            summary = "알림 읽음 처리",
            description = "특정 알림을 읽음 처리합니다. 처리 결과를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "알림 없음")
    })
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markRead(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String principal,
            @PathVariable UUID notificationId
    ) {
        UUID userId = authResolver.resolveUserId(principal, null);
        return ResponseEntity.ok(notificationService.markRead(userId, notificationId));
    }
}
