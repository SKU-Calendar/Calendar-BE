package com.example.demo.group.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Group {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "owner_user_id", nullable = false, columnDefinition = "uuid")
    private UUID ownerUserId;

    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void update(String groupName, Boolean isPublic) {
        if (groupName != null && !groupName.isBlank()) this.groupName = groupName;
        if (isPublic != null) this.isPublic = isPublic;
        this.updatedAt = LocalDateTime.now();
    }
}
