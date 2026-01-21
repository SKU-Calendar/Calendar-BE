package com.example.demo.user.service;

import com.example.demo.user.dto.UserMeUpdateRequest;
import com.example.demo.user.dto.UserMeUpdateResponse;
import com.example.demo.user.dto.UserProfileResponse;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserProfileResponse getMe(UUID userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        return toProfile(u);
    }

    @Override
    public UserProfileResponse getUserProfile(UUID targetUserId) {
        User u = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        return toProfile(u);
    }

    @Override
    public UserMeUpdateResponse updateMe(UUID userId, UserMeUpdateRequest req) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        if (req.name() != null && !req.name().isBlank()) {
            u.setName(req.name().trim());
        }

        // updatedAt 자동처리가 없다면 여기서 수동 업데이트
        u.setUpdatedAt(Instant.now());

        return new UserMeUpdateResponse(u.getId(), u.getName(), u.getUpdatedAt());
    }

    private UserProfileResponse toProfile(User u) {
        return new UserProfileResponse(u.getId(), u.getName(), u.getCreatedAt(), u.getUpdatedAt());
    }
}
