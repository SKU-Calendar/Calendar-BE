// src/main/java/com/example/demo/users/service/UserService.java
package com.example.demo.users.service;

import com.example.demo.users.dto.UpdateMyProfileRequest;
import com.example.demo.users.dto.UserProfileResponse;

import java.util.UUID;

public interface UserService {
    UserProfileResponse getMe(String principalString);
    UserProfileResponse getById(UUID userId);
    UserProfileResponse updateMe(String principalString, UpdateMyProfileRequest req);
}
