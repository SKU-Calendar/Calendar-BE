package com.example.demo.group.repository;

import com.example.demo.group.entity.GroupInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GroupInviteRepository extends JpaRepository<GroupInvite, UUID> {
    Optional<GroupInvite> findByInviteCode(String inviteCode);
    void deleteByGroupId(UUID groupId);
}
