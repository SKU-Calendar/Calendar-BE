package com.example.demo.group.dto;

import java.time.Instant;
import java.util.UUID;

public record GroupInviteResponse(
        UUID id,
        UUID groupId,
        String inviteCode,
        Instant expiresAt,
        Instant createdAt
) {}
