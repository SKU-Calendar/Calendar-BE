package com.example.demo.calendar.dto;

public class CalendarCreateRequestDto {

    private String timezone;

    protected CalendarCreateRequestDto() {
    }

    public CalendarCreateRequestDto(String timezone) {
        this.timezone = timezone;
    }

    public String getTimezone() {
        return timezone;
    }
}


