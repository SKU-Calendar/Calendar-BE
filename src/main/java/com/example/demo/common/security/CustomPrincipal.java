package com.example.demo.common.security;

import java.util.UUID;

/**
 * JWT 인증 후 SecurityContext에 저장될 Principal 객체.
 * 컨트롤러에서 @AuthenticationPrincipal로 주입받아 userId/email을 사용할 수 있다.
 */
public record CustomPrincipal(UUID userId, String email) { }
