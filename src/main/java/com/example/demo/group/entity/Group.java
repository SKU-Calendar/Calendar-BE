package com.example.demo.group.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "owner_user_id", nullable = false)
    private UUID ownerUserId;

    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Group() {}

    public Group(UUID id, UUID ownerUserId, String groupName, boolean isPublic, Instant now) {
        this.id = id;
        this.ownerUserId = ownerUserId;
        this.groupName = groupName;
        this.isPublic = isPublic;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void update(String groupName, Boolean isPublic, Instant now) {
        if (groupName != null) this.groupName = groupName;
        if (isPublic != null) this.isPublic = isPublic;
        this.updatedAt = now;
    }

    public UUID getId() { return id; }
    public UUID getOwnerUserId() { return ownerUserId; }
    public String getGroupName() { return groupName; }
    public boolean isPublic() { return isPublic; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
