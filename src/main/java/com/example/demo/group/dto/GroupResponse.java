package com.example.demo.group.dto;

import java.time.Instant;
import java.util.UUID;

public record GroupResponse(
        UUID id,
        UUID ownerUserId,
        String groupName,
        boolean isPublic,
        Instant createdAt,
        Instant updatedAt
) {}
