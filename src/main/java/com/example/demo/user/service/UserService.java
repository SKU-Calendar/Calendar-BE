package com.example.demo.user.service;

import com.example.demo.user.dto.UserMeUpdateRequest;
import com.example.demo.user.dto.UserMeUpdateResponse;
import com.example.demo.user.dto.UserProfileResponse;

import java.util.UUID;

public interface UserService {
    UserProfileResponse getMe(UUID userId);
    UserProfileResponse getUserProfile(UUID targetUserId);
    UserMeUpdateResponse updateMe(UUID userId, UserMeUpdateRequest req);
}
