package com.example.demo.group.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "group_invite",
        uniqueConstraints = @UniqueConstraint(name = "uk_group_invite_code", columnNames = {"invite_code"})
)
public class GroupInvite {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Column(name = "invite_code", nullable = false, length = 20)
    private String inviteCode;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    protected GroupInvite() {}

    public GroupInvite(UUID id, UUID groupId, String inviteCode, UUID createdBy, Instant createdAt, Instant expiresAt) {
        this.id = id;
        this.groupId = groupId;
        this.inviteCode = inviteCode;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public UUID getId() { return id; }
    public UUID getGroupId() { return groupId; }
    public String getInviteCode() { return inviteCode; }
    public UUID getCreatedBy() { return createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
}
