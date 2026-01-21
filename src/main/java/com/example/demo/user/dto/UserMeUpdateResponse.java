package com.example.demo.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserMeUpdateResponse(
        UUID id,
        String name,
        LocalDateTime updatedAt
) {}
