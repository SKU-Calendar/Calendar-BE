package com.example.demo.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateMyProfileRequest(
        @NotBlank(message = "name은 필수입니다.")
        @Size(max = 100, message = "name은 최대 100자입니다.")
        String name
) {}
