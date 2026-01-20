package com.example.demo.group.dto;

import com.example.demo.group.entity.GroupMemberRole;

import java.time.Instant;
import java.util.UUID;

public record GroupMemberResponse(
        UUID userId,
        GroupMemberRole role,
        Instant joinedAt
) {}
