package com.example.demo.event.controller;

import com.example.demo.event.dto.EventCreateRequestDto;
import com.example.demo.event.dto.EventDeleteRequestDto;
import com.example.demo.event.dto.EventResponseDto;
import com.example.demo.event.dto.EventUpdateRequestDto;
import com.example.demo.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Event", description = "캘린더 일정 생성/조회/수정/삭제 API")
@RestController
@RequestMapping("/api/calendar")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(
            summary = "일정 생성",
            description = "특정 캘린더에 일정을 생성합니다. 요청 경로의 userId는 로그인 사용자와 일치해야 하며, created_by로 저장됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = EventResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "캘린더를 찾을 수 없음")
    })
    @PostMapping("/{userId}/{calendarId}")
    public ResponseEntity<EventResponseDto> createEvent(
            @PathVariable("userId") UUID userId,
            @PathVariable("calendarId") UUID calendarId,
            @Valid @RequestBody EventCreateRequestDto request
    ) {
        EventResponseDto response = eventService.createEvent(userId, calendarId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "일정 목록 조회",
            description = "특정 캘린더에 속한 모든 일정을 조회합니다. 경로의 userId는 JWT 사용자와 일치해야 합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = EventResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "캘린더를 찾을 수 없음")
    })
    @GetMapping("/{userId}/{calendarId}")
    public ResponseEntity<List<EventResponseDto>> getEvents(
            @PathVariable("userId") UUID userId,
            @PathVariable("calendarId") UUID calendarId
    ) {
        List<EventResponseDto> responses = eventService.getEvents(userId, calendarId);
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "일정 수정",
            description = "특정 일정을 수정합니다. 캘린더 소유자 또는 일정 생성자만 수정할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = EventResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "일정을 찾을 수 없음")
    })
    @PatchMapping("/{userId}/{calendarId}")
    public ResponseEntity<EventResponseDto> updateEvent(
            @PathVariable("userId") UUID userId,
            @PathVariable("calendarId") UUID calendarId,
            @Valid @RequestBody EventUpdateRequestDto request
    ) {
        EventResponseDto response = eventService.updateEvent(userId, calendarId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "일정 삭제",
            description = "특정 일정을 완전히 삭제합니다. 캘린더 소유자 또는 일정 생성자만 삭제할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "일정을 찾을 수 없음")
    })
    @DeleteMapping("/{userId}/{calendarId}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable("userId") UUID userId,
            @PathVariable("calendarId") UUID calendarId,
            @Valid @RequestBody EventDeleteRequestDto request
    ) {
        eventService.deleteEvent(userId, calendarId, request);
        return ResponseEntity.noContent().build();
    }
}


