package com.example.demo.eventslot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "슬롯 완료 여부 변경 요청 DTO")
public class EventSlotDoneRequestDto {

    @Schema(description = "완료 여부", example = "true", required = true)
    @NotNull(message = "isDone은 필수입니다")
    @JsonProperty("isDone")
    private Boolean isDone;

    protected EventSlotDoneRequestDto() {
    }

    public EventSlotDoneRequestDto(Boolean isDone) {
        this.isDone = isDone;
    }

    @JsonProperty("isDone")
    public Boolean getIsDone() {
        return isDone;
    }

    @JsonProperty("isDone")
    public void setIsDone(Boolean isDone) {
        this.isDone = isDone;
    }
}

