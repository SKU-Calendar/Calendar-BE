// src/main/java/com/example/demo/users/dto/UserProfileResponse.java
package com.example.demo.users.dto;

import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String email,
        String name,
        Instant createdAt
) { }
