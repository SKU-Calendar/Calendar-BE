package com.example.demo.group.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "group_member",
       uniqueConstraints = @UniqueConstraint(name = "uk_group_member_group_user", columnNames = {"group_id", "user_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GroupMember {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "group_id", nullable = false, columnDefinition = "uuid")
    private UUID groupId;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "role", nullable = false, length = 20)
    private String role; // "OWNER" | "MEMBER"

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    public boolean isOwner() {
        return "OWNER".equalsIgnoreCase(role);
    }
}
