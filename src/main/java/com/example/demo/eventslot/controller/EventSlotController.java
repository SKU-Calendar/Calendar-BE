package com.example.demo.eventslot.controller;

import com.example.demo.eventslot.dto.EventSlotCreateWithoutEventRequestDto;
import com.example.demo.eventslot.dto.EventSlotDoneRequestDto;
import com.example.demo.eventslot.dto.EventSlotResponseDto;
import com.example.demo.eventslot.dto.EventSlotUpdateRequestDto;
import com.example.demo.eventslot.dto.EventSlotWithEventIdResponseDto;
import com.example.demo.eventslot.service.EventSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Event Slots", description = "이벤트 슬롯 관리 API")
@RestController
@RequestMapping("/api/event-slots")
public class EventSlotController {

    private final EventSlotService eventSlotService;

    public EventSlotController(EventSlotService eventSlotService) {
        this.eventSlotService = eventSlotService;
    }

    @Operation(
            summary = "이벤트 없이 슬롯 생성",
            description = "이벤트를 자동 생성하고 슬롯을 생성합니다. 캘린더 ID를 받아 자동으로 Event를 생성한 후 슬롯을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = EventSlotWithEventIdResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "캘린더를 찾을 수 없음"),
            @ApiResponse(responseCode = "403", description = "캘린더 접근 권한 없음")
    })
    @PostMapping
    public ResponseEntity<EventSlotWithEventIdResponseDto> createSlotWithoutEvent(
            @Valid @RequestBody EventSlotCreateWithoutEventRequestDto request
    ) {
        EventSlotWithEventIdResponseDto response = eventSlotService.createSlotWithoutEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "슬롯 수정",
            description = "특정 슬롯의 정보를 수정합니다. 수정 가능한 필드: slotStartAt, slotEndAt, slotTitle, slotNote, isDone"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = EventSlotResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "슬롯을 찾을 수 없음")
    })
    @PatchMapping("/{slotId}")
    public ResponseEntity<EventSlotResponseDto> updateSlot(
            @Parameter(description = "슬롯 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("slotId") UUID slotId,
            @Valid @RequestBody EventSlotUpdateRequestDto request
    ) {
        EventSlotResponseDto response = eventSlotService.updateSlot(slotId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "슬롯 완료 여부 변경",
            description = "슬롯의 완료 여부(isDone)만 변경합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공",
                    content = @Content(schema = @Schema(implementation = EventSlotResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "슬롯을 찾을 수 없음")
    })
    @PatchMapping("/{slotId}/done")
    public ResponseEntity<EventSlotResponseDto> updateDone(
            @Parameter(description = "슬롯 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("slotId") UUID slotId,
            @Valid @RequestBody EventSlotDoneRequestDto request
    ) {
        EventSlotResponseDto response = eventSlotService.updateDone(slotId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "슬롯 삭제",
            description = "특정 슬롯을 완전히 삭제합니다 (hard delete)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "슬롯을 찾을 수 없음")
    })
    @DeleteMapping("/{slotId}")
    public ResponseEntity<Void> deleteSlot(
            @Parameter(description = "슬롯 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("slotId") UUID slotId
    ) {
        eventSlotService.deleteSlot(slotId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

