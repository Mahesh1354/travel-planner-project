package com.travelplanner.backend.repository;
import com.travelplanner.backend.entity.UserPreference;
import com.travelplanner.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {

    // Find preferences by user
    List<UserPreference> findByUser(User user);

    // Find preferences by user ID
    List<UserPreference> findByUserId(Long userId);

    // Find preferences by user and type
    List<UserPreference> findByUserAndPreferenceType(User user, String preferenceType);

    // Find specific preference
    UserPreference findByUserAndPreferenceTypeAndPreferenceKey(
            User user, String preferenceType, String preferenceKey);
}
