package com.example.demo.timer.repository;

import com.example.demo.timer.entity.Timer;
import com.example.demo.timer.entity.TimerStatus;
import com.example.demo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TimerRepository extends JpaRepository<Timer, UUID> {

    /**
     * 특정 사용자의 특정 상태 타이머 조회
     */
    Optional<Timer> findByUserAndStatus(User user, TimerStatus status);

    /**
     * 특정 사용자의 여러 상태 중 하나인 타이머 조회
     */
    @Query("SELECT t FROM Timer t WHERE t.user = :user AND t.status IN :statuses")
    Optional<Timer> findByUserAndStatusIn(@Param("user") User user, @Param("statuses") List<TimerStatus> statuses);

    /**
     * 특정 사용자의 오늘 날짜 기준 STOPPED 상태 타이머들의 study_time 합산
     */
    @Query("SELECT COALESCE(SUM(t.studyTime), 0) FROM Timer t " +
           "WHERE t.user = :user AND t.status = :status " +
           "AND DATE(t.stoppedAt) = :date")
    Integer sumStudyTimeByUserAndStatusAndDate(
            @Param("user") User user,
            @Param("status") TimerStatus status,
            @Param("date") LocalDate date
    );
}

