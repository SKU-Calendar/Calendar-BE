package com.example.demo.auth.service;

import com.example.demo.auth.dto.AuthResponseDto;
import com.example.demo.auth.dto.LoginRequestDto;
import com.example.demo.auth.dto.SignupRequestDto;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;
    private final String issuer;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationMs,
            @Value("${jwt.issuer}") String issuer
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.issuer = issuer;
    }

    public AuthResponseDto signup(SignupRequestDto request) {
        boolean exists = userRepository.findByEmail(request.getEmail()).isPresent();
        if (exists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
        }

        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        User user = new User(
                id,
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                now,
                now
        );

        userRepository.save(user);

        return new AuthResponseDto(null, "Bearer", "SIGNUP_SUCCESS");
    }

    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        // 사용자 ID 검증: 로그인 성공했는데 사용자 ID가 없는 경우는 비정상 상황
        if (user.getId() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "User ID is missing after successful authentication. Email: " + request.getEmail());
        }

        String token = generateAccessToken(user);
        return new AuthResponseDto(token, "Bearer", "LOGIN_SUCCESS", user.getId());
    }

    public AuthResponseDto logout() {
        // 서버 상태를 저장하지 않는 로그아웃: 토큰 블랙리스트 등을 관리하지 않고 단순 성공 응답만 반환
        return new AuthResponseDto(null, "Bearer", "LOGOUT_SUCCESS");
    }

    private String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(accessTokenExpirationMs);

        return Jwts.builder()
                .subject(user.getId().toString())
                .issuer(issuer)
                .claim("email", user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }
}


