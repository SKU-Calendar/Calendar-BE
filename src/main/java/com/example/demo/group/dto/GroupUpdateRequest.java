package com.example.demo.group.dto;

public record GroupUpdateRequest(
        String groupName,
        Boolean isPublic
) { }
