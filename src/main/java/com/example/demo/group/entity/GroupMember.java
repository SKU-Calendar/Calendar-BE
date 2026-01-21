package com.example.demo.group.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "group_member",
        uniqueConstraints = @UniqueConstraint(name = "uk_group_member_group_user", columnNames = {"group_id", "user_id"})
)
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private GroupMemberStatus status = GroupMemberStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private GroupRole role = GroupRole.MEMBER;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt = Instant.now();

    protected GroupMember() {}

    public GroupMember(UUID groupId, UUID userId, GroupRole role) {
        this.groupId = groupId;
        this.userId = userId;
        this.role = role;
        this.status = GroupMemberStatus.ACTIVE;
        this.joinedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getGroupId() { return groupId; }
    public UUID getUserId() { return userId; }
    public GroupMemberStatus getStatus() { return status; }
    public GroupRole getRole() { return role; }
    public Instant getJoinedAt() { return joinedAt; }

    public boolean isOwner() { return role == GroupRole.OWNER; }
}
