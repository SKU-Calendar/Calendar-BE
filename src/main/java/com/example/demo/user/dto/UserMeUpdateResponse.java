package com.example.demo.user.dto;

import java.time.Instant;
import java.util.UUID;

public record UserMeUpdateResponse(
        UUID id,
        String name,
        Instant updatedAt
) {}
