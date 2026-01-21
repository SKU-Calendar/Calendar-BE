package com.example.demo.user.dto;

import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String name,
        Instant createdAt,
        Instant updatedAt
) {}
