package com.travelplanner.backend.repository;

import com.travelplanner.backend.entity.Notification;
import com.travelplanner.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Find notifications by user
    List<Notification> findByUser(User user);

    // Find notifications by user ID
    List<Notification> findByUserId(Long userId);

    // Find unread notifications by user
    List<Notification> findByUserAndIsReadFalse(User user);

    // Find notifications by type
    List<Notification> findByUserAndNotificationType(User user, String notificationType);

    // Find notifications that need to be sent
    @Query("SELECT n FROM Notification n WHERE n.isSent = false AND " +
            "(n.scheduledFor IS NULL OR n.scheduledFor <= :now)")
    List<Notification> findNotificationsToSend(@Param("now") LocalDateTime now);

    // Count unread notifications for user
    long countByUserAndIsReadFalse(User user);

    // Find recent notifications
    List<Notification> findByUserAndCreatedAtAfter(User user, LocalDateTime date);

    // Find notifications by related entity
    List<Notification> findByRelatedEntityTypeAndRelatedEntityId(String entityType, Long entityId);
}
