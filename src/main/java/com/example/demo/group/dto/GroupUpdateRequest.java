package com.example.demo.group.dto;

import jakarta.validation.constraints.Size;

public record GroupUpdateRequest(
        @Size(max = 100) String groupName,
        Boolean isPublic
) {}
