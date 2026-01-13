package com.example.demo.user.dto;

import java.util.UUID;

/**
 * 모바일 앱에서 사용자 정보를 조회할 때 사용할 응답 DTO.
 * 현재는 예제용으로 최소 필드만 포함합니다.
 */
public class UserResponseDto {

    private final UUID id;
    private final String email;
    private final String name;

    public UserResponseDto(UUID id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}


