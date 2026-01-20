package com.example.demo.group.repository;

import com.example.demo.group.entity.GroupMember;
import com.example.demo.group.entity.GroupMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {
    List<GroupMember> findByGroupId(UUID groupId);
    List<GroupMember> findByUserId(UUID userId);

    Optional<GroupMember> findByGroupIdAndUserId(UUID groupId, UUID userId);
    boolean existsByGroupIdAndUserId(UUID groupId, UUID userId);

    Optional<GroupMember> findByGroupIdAndUserIdAndRole(UUID groupId, UUID userId, GroupMemberRole role);

    void deleteByGroupId(UUID groupId);
    void deleteByGroupIdAndUserId(UUID groupId, UUID userId);
}
