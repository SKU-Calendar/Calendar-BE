// src/main/java/com/example/demo/user/dto/UserProfileResponse.java
package com.example.demo.user.dto;

import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String email,
        String name,
        Instant createdAt
) { }
