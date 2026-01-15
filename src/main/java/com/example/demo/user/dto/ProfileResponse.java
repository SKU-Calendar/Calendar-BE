package com.example.demo.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProfileResponse(
        UUID id,
        String email,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
