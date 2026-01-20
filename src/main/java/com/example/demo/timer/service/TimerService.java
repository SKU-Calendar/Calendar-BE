package com.example.demo.timer.service;

import com.example.demo.timer.dto.TimerResponseDto;
import com.example.demo.timer.dto.TimerStatsResponseDto;
import com.example.demo.timer.entity.Timer;
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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TimerService {

    private final TimerRepository timerRepository;
    private final UserRepository userRepository;

    public TimerService(TimerRepository timerRepository, UserRepository userRepository) {
        this.timerRepository = timerRepository;
        this.userRepository = userRepository;
    }

    /**
     * 타이머 시작
     * - 이미 RUNNING 상태의 타이머가 있으면 에러
     * - 새로운 timer row 생성
     * - status = RUNNING
     * - start_at = now
     * - last_started_at = now
     */
    @Transactional
    public TimerResponseDto startTimer() {
        User currentUser = getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        // 이미 RUNNING 상태의 타이머가 있는지 확인
        Optional<Timer> existingRunningTimer = timerRepository.findByUserAndStatus(currentUser, TimerStatus.RUNNING);
        if (existingRunningTimer.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 실행 중인 타이머가 있습니다.");
        }

        Timer timer = new Timer(
                UUID.randomUUID(),
                currentUser,
                TimerStatus.RUNNING,
                now,
                now,
                null,
                0
        );

        Timer saved = timerRepository.save(timer);
        return toResponse(saved);
    }

    /**
     * 타이머 일시정지
     * - RUNNING 상태의 타이머 1개 조회
     * - study_time += (now - last_started_at) 초 단위
     * - status = PAUSED
     * - last_started_at = null
     */
    @Transactional
    public TimerResponseDto pauseTimer() {
        User currentUser = getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        Timer timer = timerRepository.findByUserAndStatus(currentUser, TimerStatus.RUNNING)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "실행 중인 타이머가 없습니다."));

        // study_time 누적 계산
        if (timer.getLastStartedAt() != null) {
            Duration duration = Duration.between(timer.getLastStartedAt(), now);
            int seconds = (int) duration.getSeconds();
            timer.accumulateStudyTime(seconds);
        }

        timer.pause();
        Timer saved = timerRepository.save(timer);
        return toResponse(saved);
    }

    /**
     * 타이머 재개
     * - PAUSED 상태의 타이머 조회
     * - status = RUNNING
     * - last_started_at = now
     */
    @Transactional
    public TimerResponseDto resumeTimer() {
        User currentUser = getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        Timer timer = timerRepository.findByUserAndStatus(currentUser, TimerStatus.PAUSED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일시정지된 타이머가 없습니다."));

        timer.resume(now);
        Timer saved = timerRepository.save(timer);
        return toResponse(saved);
    }

    /**
     * 타이머 중지
     * - RUNNING 또는 PAUSED 상태 타이머 조회
     * - RUNNING 상태라면 study_time 누적
     * - status = STOPPED
     * - stopped_at = now
     * - last_started_at = null
     */
    @Transactional
    public TimerResponseDto stopTimer() {
        User currentUser = getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        Timer timer = timerRepository.findByUserAndStatusIn(
                currentUser,
                List.of(TimerStatus.RUNNING, TimerStatus.PAUSED)
        ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "실행 중이거나 일시정지된 타이머가 없습니다."));

        // RUNNING 상태라면 study_time 누적
        if (timer.getStatus() == TimerStatus.RUNNING && timer.getLastStartedAt() != null) {
            Duration duration = Duration.between(timer.getLastStartedAt(), now);
            int seconds = (int) duration.getSeconds();
            timer.accumulateStudyTime(seconds);
        }

        timer.stop(now);
        Timer saved = timerRepository.save(timer);
        return toResponse(saved);
    }

    /**
     * 오늘 공부 시간 통계 조회
     * - 로그인한 사용자 기준
     * - STOPPED 상태의 타이머들 study_time 합산
     * - 오늘 기준 공부 시간 반환
     */
    @Transactional(readOnly = true)
    public TimerStatsResponseDto getTodayStats() {
        User currentUser = getCurrentUser();
        LocalDate today = LocalDate.now();

        Integer totalStudyTime = timerRepository.sumStudyTimeByUserAndStatusAndDate(
                currentUser,
                TimerStatus.STOPPED,
                today
        );

        return new TimerStatsResponseDto(totalStudyTime);
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

    private TimerResponseDto toResponse(Timer timer) {
        return new TimerResponseDto(
                timer.getId(),
                timer.getUser().getId(),
                timer.getStatus().name(),
                timer.getStartAt(),
                timer.getLastStartedAt(),
                timer.getStoppedAt(),
                timer.getStudyTime(),
                timer.getCreatedAt(),
                timer.getUpdatedAt()
        );
    }
}

