package com.example.demo.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "채팅 메시지 전송 요청 DTO")
public class ChatRequestDto {

    @Schema(description = "사용자 메시지", required = true, example = "내일 오후 2시에 회의 일정 만들어줘")
    @NotBlank(message = "메시지는 필수입니다")
    private String message;

    @Schema(description = "캘린더 ID", required = true)
    @NotNull(message = "캘린더 ID는 필수입니다")
    private UUID calendarId;

    protected ChatRequestDto() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UUID getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(UUID calendarId) {
        this.calendarId = calendarId;
    }
}

