package com.example.demo.group.repository;

import com.example.demo.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {

    // 내 멤버십 기반 조회에서 사용
    List<Group> findByIdIn(Collection<UUID> ids);

    // ✅ 공개 그룹 전체
    List<Group> findByIsPublicTrue();
}
