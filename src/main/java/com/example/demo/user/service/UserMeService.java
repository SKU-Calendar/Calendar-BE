package com.example.demo.user.service;

import com.example.demo.user.domain.User;
import com.example.demo.user.dto.ProfileResponse;
import com.example.demo.user.dto.ProfileUpdateRequest;
import com.example.demo.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserMeService {

    private final UserRepository userRepository;

    public UserMeService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ProfileResponse getMe(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return toResponse(user);
    }

    @Transactional
    public ProfileResponse updateMe(UUID userId, ProfileUpdateRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(req.name()); // 변경 감지(dirty checking)로 UPDATE 발생
        return toResponse(user);
    }

    private ProfileResponse toResponse(User u) {
        return new ProfileResponse(
                u.getId(),
                u.getEmail(),
                u.getName(),
                u.getCreatedAt(),
                u.getUpdatedAt()
        );
    }
}
