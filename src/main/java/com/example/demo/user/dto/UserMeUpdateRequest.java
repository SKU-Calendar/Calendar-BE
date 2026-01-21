package com.example.demo.user.dto;

import jakarta.validation.constraints.Size;

public record UserMeUpdateRequest(
        @Size(min = 1, max = 100)
        String name
) {}
