package com.example.demo.social.dto;

import java.util.UUID;

public record SocialUserProfileDto(
        UUID userId,
        String name,
        String email
) {
}


