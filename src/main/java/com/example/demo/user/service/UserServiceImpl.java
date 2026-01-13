package com.example.demo.user.service;

import com.example.demo.user.dto.UserResponseDto;
import com.example.demo.user.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * {@link UserService}의 기본 구현체.
 * 현재는 예제용 더미 데이터를 반환합니다.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDto getSampleUser() {
        // 아직 DB 연동은 하지 않고, 구조 확인용 더미 데이터만 반환
        return new UserResponseDto(
                null,
                "sample@example.com",
                "Sample User"
        );
    }
}


