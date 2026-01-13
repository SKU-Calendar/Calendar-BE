package com.example.demo.auth.controller;

import com.example.demo.auth.dto.AuthResponseDto;
import com.example.demo.auth.dto.LoginRequestDto;
import com.example.demo.auth.dto.SignupRequestDto;
import com.example.demo.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "회원가입/로그인/로그아웃 인증 API")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 생성합니다. 이메일 중복을 검사하고, 비밀번호는 BCrypt로 암호화합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "가입 성공",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> signup(@Valid @RequestBody SignupRequestDto request) {
        AuthResponseDto response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "로그인",
            description = "이메일/비밀번호를 검증하여 JWT 액세스 토큰을 발급합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        AuthResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "로그아웃",
            description = "서버 상태를 저장하지 않고 단순 200 OK를 반환하여 클라이언트 측 토큰을 폐기하도록 합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 응답")
    })
    @PostMapping("/logout")
    public ResponseEntity<AuthResponseDto> logout() {
        AuthResponseDto response = authService.logout();
        return ResponseEntity.ok(response);
    }
}


