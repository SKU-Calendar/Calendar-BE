package com.example.demo.auth.dto;

import java.util.UUID;

public class AuthResponseDto {

    private final String accessToken;
    private final String tokenType;
    private final String message;
    private final UUID userId;

    public AuthResponseDto(String accessToken, String tokenType, String message) {
        this(accessToken, tokenType, message, null);
    }

    public AuthResponseDto(String accessToken, String tokenType, String message, UUID userId) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.message = message;
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getMessage() {
        return message;
    }

    public UUID getUserId() {
        return userId;
    }
}


