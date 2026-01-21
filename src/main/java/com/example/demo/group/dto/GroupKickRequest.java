package com.example.demo.group.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record GroupKickRequest(
        @NotNull UUID userId
) {}
