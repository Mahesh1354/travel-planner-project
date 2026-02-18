package com.travelplanner.backend.repository;

import com.travelplanner.backend.entity.SearchPreference;
import com.travelplanner.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchPreferenceRepository extends JpaRepository<SearchPreference, Long> {

    // Find all preferences for a user
    List<SearchPreference> findByUser(User user);

    // Find preferences for a user ordered by priority (descending)
    List<SearchPreference> findByUserOrderByPriorityDesc(User user);

    // Find preferences by user and type
    List<SearchPreference> findByUserAndPreferenceType(User user, String preferenceType);

    // Find preferences by user and type ordered by priority (descending) - ADD THIS METHOD
    List<SearchPreference> findByUserAndPreferenceTypeOrderByPriorityDesc(User user, String preferenceType);

    // Find specific preference by user, type, and key - returns Optional
    Optional<SearchPreference> findByUserAndPreferenceTypeAndPreferenceKey(
            User user, String preferenceType, String preferenceKey);
}