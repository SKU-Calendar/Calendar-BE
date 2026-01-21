package com.example.demo.group.dto;

import jakarta.validation.constraints.NotBlank;

public record GroupCreateRequest(
        @NotBlank String groupName,
        boolean isPublic
) {}
