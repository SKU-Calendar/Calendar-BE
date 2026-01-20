package com.example.demo.timer.controller;

import com.example.demo.timer.dto.TimerResponseDto;
import com.example.demo.timer.dto.TimerStatsResponseDto;
import com.example.demo.timer.service.TimerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Timer", description = "타이머 시작/일시정지/재개/중지 및 통계 조회 API")
@RestController
@RequestMapping("/api/timer")
public class TimerController {

    private final TimerService timerService;

    public TimerController(TimerService timerService) {
        this.timerService = timerService;
    }

    @Operation(
            summary = "타이머 시작",
            description = "새로운 타이머를 시작합니다. 이미 실행 중인 타이머가 있으면 에러가 발생합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "시작 성공",
                    content = @Content(schema = @Schema(implementation = TimerResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "이미 실행 중인 타이머가 있음"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/start")
    public ResponseEntity<TimerResponseDto> startTimer() {
        TimerResponseDto response = timerService.startTimer();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "타이머 일시정지",
            description = "실행 중인 타이머를 일시정지합니다. study_time이 자동으로 누적됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일시정지 성공",
                    content = @Content(schema = @Schema(implementation = TimerResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "실행 중인 타이머가 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/pause")
    public ResponseEntity<TimerResponseDto> pauseTimer() {
        TimerResponseDto response = timerService.pauseTimer();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "타이머 재개",
            description = "일시정지된 타이머를 재개합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재개 성공",
                    content = @Content(schema = @Schema(implementation = TimerResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "일시정지된 타이머가 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/resume")
    public ResponseEntity<TimerResponseDto> resumeTimer() {
        TimerResponseDto response = timerService.resumeTimer();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "타이머 중지",
            description = "실행 중이거나 일시정지된 타이머를 중지합니다. 실행 중이었다면 study_time이 자동으로 누적됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "중지 성공",
                    content = @Content(schema = @Schema(implementation = TimerResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "실행 중이거나 일시정지된 타이머가 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/stop")
    public ResponseEntity<TimerResponseDto> stopTimer() {
        TimerResponseDto response = timerService.stopTimer();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "오늘 공부 시간 통계 조회",
            description = "오늘 날짜 기준으로 STOPPED 상태의 타이머들의 study_time 합산을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = TimerStatsResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/stats")
    public ResponseEntity<TimerStatsResponseDto> getTodayStats() {
        TimerStatsResponseDto response = timerService.getTodayStats();
        return ResponseEntity.ok(response);
    }
}

