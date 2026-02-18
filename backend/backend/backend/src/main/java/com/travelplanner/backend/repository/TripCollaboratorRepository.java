package com.travelplanner.backend.repository;

import com.travelplanner.backend.entity.Trip;
import com.travelplanner.backend.entity.TripCollaborator;
import com.travelplanner.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripCollaboratorRepository extends JpaRepository<TripCollaborator, Long> {

    // Find all collaborators for a trip
    List<TripCollaborator> findByTrip(Trip trip);

    // Find all collaborations for a user
    List<TripCollaborator> findByUser(User user);

    // Find specific collaborator by trip and user
    Optional<TripCollaborator> findByTripAndUser(Trip trip, User user);

    // Check if a user is a collaborator on a trip
    boolean existsByTripAndUser(Trip trip, User user);

    // Check if a user is an accepted collaborator on a trip (by email)
    @Query("SELECT CASE WHEN COUNT(tc) > 0 THEN true ELSE false END FROM TripCollaborator tc " +
            "WHERE tc.trip = :trip AND tc.user.email = :userEmail AND tc.isAccepted = true")
    boolean existsByTripAndUserEmailAndIsAcceptedTrue(@Param("trip") Trip trip, @Param("userEmail") String userEmail);

    // Check if a user has edit permission on a trip
    @Query("SELECT CASE WHEN COUNT(tc) > 0 THEN true ELSE false END FROM TripCollaborator tc " +
            "WHERE tc.trip = :trip AND tc.user.email = :userEmail " +
            "AND tc.permissionLevel IN :permissionLevels AND tc.isAccepted = true")
    boolean existsByTripAndUserEmailAndPermissionLevelInAndIsAcceptedTrue(
            @Param("trip") Trip trip,
            @Param("userEmail") String userEmail,
            @Param("permissionLevels") List<TripCollaborator.PermissionLevel> permissionLevels);

    // Delete all collaborators for a trip - ADD THIS METHOD
    @Transactional
    @Modifying
    @Query("DELETE FROM TripCollaborator tc WHERE tc.trip = :trip")
    void deleteByTrip(@Param("trip") Trip trip);

    // Alternative simpler method if you prefer (Spring Data JPA will parse this)
    // void deleteByTrip(Trip trip);
}