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

    public String requirePrincipal(String principal) {
        if (principal == null || principal.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return principal;
    }

    /**
     * JwtAuthenticationFilter가 principal에 넣는 값은 String이다.
     * - email claim 있으면: email
     * - 없으면: subject
     *
     * 여기서는 principal이 UUID 문자열이면 그대로 파싱하고,
     * 아니면 email로 보고 DB에서 userId를 조회한다.
     */
    public UUID requireUserId(String principal) {
        requirePrincipal(principal);

        // subject가 UUID로 들어오는 토큰도 대비
        try {
            return UUID.fromString(principal);
        } catch (IllegalArgumentException ignore) {
            return userRepository.findIdByEmail(principal)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));
        }
    }
}
