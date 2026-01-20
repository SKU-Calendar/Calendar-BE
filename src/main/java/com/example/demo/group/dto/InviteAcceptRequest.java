package com.example.demo.group.dto;

import jakarta.validation.constraints.NotBlank;

public record InviteAcceptRequest(
        @NotBlank String inviteCode
) {}
