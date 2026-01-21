package com.example.demo.social.controller;

import com.example.demo.social.dto.FriendStatsResponseDto;
import com.example.demo.social.service.SocialStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Social", description = "친구(그룹 멤버) 프로필 및 공부 통계 조회 API")
@RestController
@RequestMapping("/api/social")
public class SocialController {

    private final SocialStatsService socialStatsService;

    public SocialController(SocialStatsService socialStatsService) {
        this.socialStatsService = socialStatsService;
    }

    @Operation(
            summary = "친구 공부 통계 조회",
            description = "대상 사용자(userId)의 프로필과 공부 시간 통계(오늘/최근 7일/이번 달/전체 누적)를 한 번에 조회합니다. " +
                    "요청자는 JWT 인증된 사용자여야 하며, 이번 구현에서는 요청자-대상 간 친구/그룹 관계 검증을 하지 않습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = FriendStatsResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "대상 사용자 없음")
    })
    @GetMapping("/{userId}/stats")
    public ResponseEntity<FriendStatsResponseDto> getFriendStats(@PathVariable UUID userId) {
        FriendStatsResponseDto response = socialStatsService.getFriendStats(userId);
        return ResponseEntity.ok(response);
    }
}


