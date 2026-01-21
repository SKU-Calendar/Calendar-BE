package com.example.demo.notification.support;

import com.example.demo.common.exception.ApiException;
import com.example.demo.common.exception.ErrorCode;
import com.example.demo.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class NotificationAuthResolver {

    private final UserRepository userRepository;
    private final SecretKey secretKey;
    private final String issuer;

    public NotificationAuthResolver(
            UserRepository userRepository,
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.issuer}") String issuer
    ) {
        this.userRepository = userRepository;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
    }

    /** REST: principal(email) 우선, 없으면 token으로 email 추출 */
    public UUID resolveUserId(String principalEmail, String token) {
        String email = principalEmail;

        if ((email == null || email.isBlank()) && token != null && !token.isBlank()) {
            email = extractEmail(token);
        }

        if (email == null || email.isBlank()) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        return userRepository.findByEmail(email)
                .map(u -> u.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.UNAUTHORIZED, "사용자 정보를 찾을 수 없습니다."));
    }

    /** WS: token에서 email만 뽑아서 userId resolve */
    public UUID resolveUserIdFromTokenOnly(String token) {
        if (token == null || token.isBlank()) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "토큰이 필요합니다.");
        }
        String email = extractEmail(token);
        return userRepository.findByEmail(email)
                .map(u -> u.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.UNAUTHORIZED, "사용자 정보를 찾을 수 없습니다."));
    }

    private String extractEmail(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String email = claims.get("email", String.class);
            if (email != null && !email.isBlank()) return email;

            // 과거/예외 토큰 대비: subject에 email이 들어있는 케이스
            return claims.getSubject();
        } catch (JwtException e) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        }
    }
}
