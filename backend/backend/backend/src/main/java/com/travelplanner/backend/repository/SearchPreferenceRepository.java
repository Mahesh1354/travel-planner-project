package com.travelplanner.backend.repository;

import com.travelplanner.backend.entity.SearchPreference;
import com.travelplanner.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SearchPreferenceRepository extends JpaRepository<SearchPreference, Long> {

    // Find preferences by user
    List<SearchPreference> findByUser(User user);

    // Find preferences by user ID
    List<SearchPreference> findByUserId(Long userId);

    // Find preferences by user and type
    List<SearchPreference> findByUserAndPreferenceType(User user, String preferenceType);

    // Find preferences by user, type, and key
    SearchPreference findByUserAndPreferenceTypeAndPreferenceKey(
            User user, String preferenceType, String preferenceKey);
}
