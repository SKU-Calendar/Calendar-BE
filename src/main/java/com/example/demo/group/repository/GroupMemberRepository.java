package com.example.demo.group.repository;

import com.example.demo.group.entity.GroupMember;
import com.example.demo.group.entity.GroupMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // ✅ 추가: 그룹의 OWNER 멤버 조회
    Optional<GroupMember> findFirstByGroupIdAndRole(UUID groupId, GroupMemberRole role);

    // ✅ 추가: 그룹 OWNER의 userId(UUID)만 바로 조회 (가장 깔끔)
    @Query("select gm.userId from GroupMember gm where gm.groupId = :groupId and gm.role = :role")
    Optional<UUID> findOwnerUserId(@Param("groupId") UUID groupId, @Param("role") GroupMemberRole role);
}
