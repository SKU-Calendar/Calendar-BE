package com.example.demo.group.repository;

import com.example.demo.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {
    List<Group> findByIdIn(Collection<UUID> ids);
}
