package com.example.demo.group.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "owner_user_id", nullable = false)
    private UUID ownerUserId;

    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected Group() {}

    public Group(UUID ownerUserId, String groupName, boolean isPublic) {
        this.ownerUserId = ownerUserId;
        this.groupName = groupName;
        this.isPublic = isPublic;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getOwnerUserId() { return ownerUserId; }
    public String getGroupName() { return groupName; }
    public boolean isPublic() { return isPublic; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void update(String groupName, Boolean isPublic) {
        if (groupName != null && !groupName.isBlank()) this.groupName = groupName;
        if (isPublic != null) this.isPublic = isPublic;
    }
}
