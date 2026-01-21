// src/main/java/com/example/demo/users/service/UserServiceImpl.java
package com.example.demo.users.service;

import com.example.demo.users.dto.UpdateMyProfileRequest;
import com.example.demo.users.dto.UserProfileResponse;
import com.example.demo.users.entity.User;
import com.example.demo.users.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private User resolveCurrentUser(String principalString) {
        if (principalString == null || principalString.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        // 1) UUID로 파싱되면 id로 조회
        try {
            UUID userId = UUID.fromString(principalString);
            return userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 사용자입니다."));
        } catch (IllegalArgumentException ignore) {
            // 2) UUID가 아니면 email로 조회
            return userRepository.findByEmail(principalString)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 사용자입니다."));
        }
    }

    private UserProfileResponse toProfile(User u) {
        return new UserProfileResponse(
                u.getId(),
                u.getEmail(),
                u.getName(),
                u.getCreatedAt()
        );
    }

    @Override
    public UserProfileResponse getMe(String principalString) {
        User me = resolveCurrentUser(principalString);
        return toProfile(me);
    }

    @Override
    public UserProfileResponse getById(UUID userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        return toProfile(u);
    }

    @Override
    @Transactional
    public UserProfileResponse updateMe(String principalString, UpdateMyProfileRequest req) {
        User me = resolveCurrentUser(principalString);

        me.setName(req.name());
        me.setUpdatedAt(Instant.now());

        // dirty checking
        return toProfile(me);
    }
}
