package com.example.demo.group.dto;

import java.time.LocalDateTime;

public record InviteCreateResponse(
        String inviteCode,
        LocalDateTime expiresAt
) { }
