package com.example.demo.calendar.controller;

import com.example.demo.calendar.dto.CalendarCreateRequestDto;
import com.example.demo.calendar.dto.CalendarDetailResponseDto;
import com.example.demo.calendar.dto.CalendarResponseDto;
import com.example.demo.calendar.service.CalendarService;
import com.example.demo.eventslot.dto.EventSlotWithEventIdResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Calendar", description = "캘린더 생성/조회 및 날짜별 슬롯 조회 API")
@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @Operation(
            summary = "캘린더 생성",
            description = "로그인한 사용자를 owner로 하여 새로운 캘린더를 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = CalendarResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<CalendarResponseDto> createCalendar(
            @Valid @RequestBody CalendarCreateRequestDto request
    ) {
        CalendarResponseDto response = calendarService.createCalendar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "내 캘린더 목록 조회",
            description = "로그인한 사용자가 소유한 모든 캘린더 목록을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CalendarResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<CalendarResponseDto>> getMyCalendars() {
        List<CalendarResponseDto> calendars = calendarService.getMyCalendars();
        return ResponseEntity.ok(calendars);
    }

    @Operation(
            summary = "캘린더 상세 조회",
            description = "지정한 캘린더의 기본 정보와 포함된 이벤트/슬롯 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CalendarDetailResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "캘린더를 찾을 수 없음")
    })
    @GetMapping("/{calendarId}")
    public ResponseEntity<CalendarDetailResponseDto> getCalendarDetail(
            @PathVariable("calendarId") UUID calendarId
    ) {
        CalendarDetailResponseDto response = calendarService.getCalendarDetail(calendarId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "특정 날짜 슬롯 조회",
            description = "캘린더 ID와 날짜를 기준으로 해당 날짜의 슬롯(event_slots) 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = EventSlotWithEventIdResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "캘린더를 찾을 수 없음")
    })
    @GetMapping("/{calendarId}/day/{date}")
    public ResponseEntity<List<EventSlotWithEventIdResponseDto>> getSlotsByDay(
            @PathVariable("calendarId") UUID calendarId,
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<EventSlotWithEventIdResponseDto> response = calendarService.getSlotsByDay(calendarId, date);
        return ResponseEntity.ok(response);
    }
}


