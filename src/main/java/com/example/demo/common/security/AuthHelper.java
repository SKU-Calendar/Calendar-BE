package com.example.demo.common.security;

import com.example.demo.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class AuthHelper {

    private final UserRepository userRepository;

    public AuthHelper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** principal(String)이 null이면 401 */
    public String requirePrincipal(String principal) {
        if (principal == null || principal.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return principal;
    }

    /** principal(String=email or UUID 문자열) -> userId(UUID) */
    public UUID requireUserId(String principal) {
        requirePrincipal(principal);

        // 1) principal이 UUID면 바로 파싱
        try {
            return UUID.fromString(principal);
        } catch (IllegalArgumentException ignore) {
            // 2) 아니면 이메일로 보고 DB 조회
            return userRepository.findIdByEmail(principal)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));
        }
    }
}
