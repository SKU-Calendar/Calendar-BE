package com.example.demo.group.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record GroupMemberResponse(
        UUID userId,
        String role,
        LocalDateTime joinedAt
) { }
