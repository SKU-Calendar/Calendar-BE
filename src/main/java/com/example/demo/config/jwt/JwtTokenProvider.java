package com.example.demo.config.jwt;

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
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final String issuer;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.issuer}") String issuer
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
    }

    public UUID getUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String subject = claims.getSubject();
            if (subject == null) return null;
            return UUID.fromString(subject);
        } catch (IllegalArgumentException | JwtException e) {
            return null;
        }
    }

    /**
     * Refresh Token을 검증하고 Claims를 반환합니다.
     * 서명 검증, 만료 여부 확인, issuer 확인을 수행합니다.
     *
     * @param refreshToken 검증할 Refresh Token
     * @return 검증된 Claims
     * @throws JwtException 토큰 검증 실패 시 (서명 오류, 만료, issuer 불일치 등)
     */
    public Claims validateRefreshToken(String refreshToken) throws JwtException {
        return Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();
    }
}
