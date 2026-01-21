package com.example.demo.group.dto;

import java.time.Instant;
import java.util.UUID;

public record GroupMemberResponse(
        UUID userId,
        String role,
        String status,
        Instant joinedAt
) {}
