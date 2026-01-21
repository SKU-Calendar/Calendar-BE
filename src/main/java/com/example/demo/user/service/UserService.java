package com.example.demo.user.service;

import com.example.demo.user.dto.UpdateMyProfileRequest;
import com.example.demo.user.dto.UserProfileResponse;

import java.util.UUID;

public interface UserService {
    UserProfileResponse getMe(String principalString);
    UserProfileResponse getById(UUID userId);
    UserProfileResponse updateMe(String principalString, UpdateMyProfileRequest req);
}
