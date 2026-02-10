package com.travelplanner.backend.repository;

import com.travelplanner.backend.entity.NotificationPreference;
import com.travelplanner.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {

    // Find preferences by user
    List<NotificationPreference> findByUser(User user);

    // Find preference by user and type
    Optional<NotificationPreference> findByUserAndNotificationType(User user, String notificationType);

    // Find enabled preferences by user
    List<NotificationPreference> findByUserAndEnabledTrue(User user);

    // Check if user has preference enabled for type
    boolean existsByUserAndNotificationTypeAndEnabledTrue(User user, String notificationType);
}
