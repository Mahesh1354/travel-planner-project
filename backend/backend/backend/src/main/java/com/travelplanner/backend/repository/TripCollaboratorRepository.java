package com.travelplanner.backend.repository;


import com.travelplanner.backend.entity.Trip;
import com.travelplanner.backend.entity.TripCollaborator;
import com.travelplanner.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripCollaboratorRepository extends JpaRepository<TripCollaborator, Long> {

    // Find collaborators by trip
    List<TripCollaborator> findByTrip(Trip trip);

    // Find collaborators by user
    List<TripCollaborator> findByUser(User user);

    // Find specific collaborator by trip and user
    Optional<TripCollaborator> findByTripAndUser(Trip trip, User user);

    // Check if user is collaborator for trip
    boolean existsByTripAndUser(Trip trip, User user);

    // Find accepted collaborators by trip
    List<TripCollaborator> findByTripAndIsAcceptedTrue(Trip trip);
}