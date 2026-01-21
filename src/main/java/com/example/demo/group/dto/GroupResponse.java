package com.example.demo.group.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record GroupResponse(
        UUID id,
        UUID ownerUserId,
        String groupName,
        boolean isPublic,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        long memberCount,
        String myRole // null 가능(전체 조회 시)
) { }
