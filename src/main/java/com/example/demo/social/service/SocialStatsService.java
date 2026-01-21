package com.example.demo.social.service;

import com.example.demo.social.dto.FriendStatsResponseDto;
import com.example.demo.social.dto.SocialUserProfileDto;
import com.example.demo.timer.dto.TimerStatsResponseDto;
import com.example.demo.timer.entity.TimerStatus;
import com.example.demo.timer.repository.TimerRepository;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class SocialStatsService {

    private final TimerRepository timerRepository;
    private final UserRepository userRepository;

    public SocialStatsService(TimerRepository timerRepository, UserRepository userRepository) {
        this.timerRepository = timerRepository;
        this.userRepository = userRepository;
    }

    /**
     * 친구(대상 사용자)의 프로필 + 공부 시간 통계 조회
     * - 요청자는 JWT 인증된 사용자 (친구/그룹 관계 검증은 하지 않음)
     * - 대상 userId 기준으로 STOPPED 타이머들의 study_time 합산
     * - 오늘, 최근 7일, 이번 달, 전체 누적을 한 번에 반환
     */
    @Transactional(readOnly = true)
    public FriendStatsResponseDto getFriendStats(UUID userId) {
        // 인증 강제 (요청자는 JWT 인증 사용자여야 함)
        getCurrentUser();

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        SocialUserProfileDto profile = new SocialUserProfileDto(
                targetUser.getId(),
                targetUser.getName(),
                targetUser.getEmail()
        );

        LocalDate today = LocalDate.now();

        Integer todayStudyTime = timerRepository.sumStudyTimeByUserAndStatusAndDate(
                targetUser,
                TimerStatus.STOPPED,
                today
        );

        LocalDate weekStart = today.minusDays(6); // 오늘 포함 최근 7일
        Integer weeklyStudyTime = timerRepository.sumStudyTimeByUserAndStatusAndDateRange(
                targetUser,
                TimerStatus.STOPPED,
                weekStart,
                today
        );

        Integer monthlyStudyTime = timerRepository.sumStudyTimeByUserAndStatusAndYearMonth(
                targetUser,
                TimerStatus.STOPPED,
                today.getYear(),
                today.getMonthValue()
        );

        Integer totalStudyTime = timerRepository.sumStudyTimeByUserAndStatus(
                targetUser,
                TimerStatus.STOPPED
        );

        TimerStatsResponseDto stats = new TimerStatsResponseDto(
                todayStudyTime,
                weeklyStudyTime,
                monthlyStudyTime,
                totalStudyTime
        );

        return new FriendStatsResponseDto(profile, stats);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}


