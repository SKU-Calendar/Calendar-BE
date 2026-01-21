package com.example.demo.group.repository;

import com.example.demo.group.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {

    long countByGroupId(UUID groupId);

    Optional<GroupMember> findByGroupIdAndUserId(UUID groupId, UUID userId);

    List<GroupMember> findByGroupIdOrderByJoinedAtAsc(UUID groupId);

    List<GroupMember> findByUserIdOrderByJoinedAtDesc(UUID userId);

    void deleteByGroupIdAndUserId(UUID groupId, UUID userId);
}
