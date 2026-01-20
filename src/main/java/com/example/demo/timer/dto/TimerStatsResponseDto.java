package com.example.demo.timer.dto;

public record TimerStatsResponseDto(
        Integer todayStudyTime,
        Integer weeklyStudyTime,
        Integer monthlyStudyTime,
        Integer totalStudyTime
) {
}

