package com.example.demo.group.dto;

import java.time.Instant;
import java.util.UUID;

public record GroupDetailResponse(
        UUID id,
        String groupName,
        boolean isPublic,
        UUID ownerUserId,
        long memberCount,
        String myRole,
        Instant createdAt,
        Instant updatedAt
) {}
