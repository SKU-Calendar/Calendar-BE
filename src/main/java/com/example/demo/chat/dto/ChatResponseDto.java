package com.example.demo.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "채팅 메시지 응답 DTO")
public class ChatResponseDto {

    @Schema(description = "메시지 ID")
    private final UUID id;

    @Schema(description = "채팅 ID")
    private final UUID chatId;

    @Schema(description = "메시지 역할", example = "USER", allowableValues = {"USER", "ASSISTANT", "SYSTEM"})
    private final String role;

    @Schema(description = "메시지 내용")
    private final String content;

    @Schema(description = "생성 시간")
    private final LocalDateTime createdAt;

    public ChatResponseDto(UUID id, UUID chatId, String role, String content, LocalDateTime createdAt) {
        this.id = id;
        this.chatId = chatId;
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getChatId() {
        return chatId;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

