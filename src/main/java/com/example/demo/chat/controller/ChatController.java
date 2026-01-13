package com.example.demo.chat.controller;

import com.example.demo.chat.dto.ChatRequestDto;
import com.example.demo.chat.dto.ChatResponseDto;
import com.example.demo.chat.service.ChatService;
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

import java.util.List;
import java.util.UUID;

@Tag(name = "Chat", description = "AI 채팅 기반 일정 생성 API")
@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(
            summary = "채팅 메시지 전송 및 일정 생성",
            description = "사용자의 자연어 입력을 받아 OpenAI API를 호출하여 일정(Event)과 슬롯(Slot)을 생성합니다. " +
                    "생성된 Event와 Slot을 JPA 엔티티로 저장하고, 채팅 메시지(USER / AI)를 DB에 저장합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "일정 생성 성공",
                    content = @Content(schema = @Schema(implementation = ChatResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "AI 응답 파싱 실패 또는 일정 생성 실패"),
            @ApiResponse(responseCode = "404", description = "캘린더를 찾을 수 없음"),
            @ApiResponse(responseCode = "403", description = "캘린더 접근 권한 없음")
    })
    @PostMapping("/{chatId}")
    public ResponseEntity<ChatResponseDto> sendMessage(
            @Parameter(description = "채팅 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("chatId") UUID chatId,
            @Valid @RequestBody ChatRequestDto request
    ) {
        ChatResponseDto response = chatService.sendMessage(chatId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "채팅 메시지 목록 조회",
            description = "특정 chatId에 해당하는 모든 채팅 메시지 목록을 생성 시간 순으로 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ChatResponseDto.class)))
    })
    @GetMapping("/{chatId}")
    public ResponseEntity<List<ChatResponseDto>> getMessages(
            @Parameter(description = "채팅 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("chatId") UUID chatId
    ) {
        List<ChatResponseDto> responses = chatService.getMessages(chatId);
        return ResponseEntity.ok(responses);
    }
}

