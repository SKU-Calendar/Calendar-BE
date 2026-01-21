package com.example.demo.notification.repository;

import com.example.demo.notification.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @Query("""
        select n
        from Notification n
        where n.userId = :userId
        order by n.isRead asc, n.createdAt desc
    """)
    List<Notification> findMyNotifications(UUID userId, Pageable pageable);

    Optional<Notification> findByIdAndUserId(UUID id, UUID userId);

    long countByUserIdAndIsReadFalse(UUID userId);
}
