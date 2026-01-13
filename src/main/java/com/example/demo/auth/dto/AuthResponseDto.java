package com.example.demo.auth.dto;

public class AuthResponseDto {

    private final String accessToken;
    private final String tokenType;
    private final String message;

    public AuthResponseDto(String accessToken, String tokenType, String message) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.message = message;
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
}


