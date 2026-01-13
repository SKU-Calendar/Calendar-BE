package com.example.demo.event.entity;

/**
 * Event의 상태를 나타내는 enum.
 * DB CHECK 제약조건과 일치해야 합니다: PLANNED, CONFIRMED, DONE, CANCELLED
 */
public enum EventStatus {
    PLANNED,
    CONFIRMED,
    DONE,
    CANCELLED
}

