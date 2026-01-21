package com.example.demo.social.dto;

import com.example.demo.timer.dto.TimerStatsResponseDto;

public record FriendStatsResponseDto(
        SocialUserProfileDto profile,
        TimerStatsResponseDto stats
) {
}


