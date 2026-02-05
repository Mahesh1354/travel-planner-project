package com.travelplanner.backend.repository;


import com.travelplanner.backend.entity.Trip;
import com.travelplanner.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    // Find trips by user
    List<Trip> findByUser(User user);

    // Find trips by user ID
    List<Trip> findByUserId(Long userId);

    // Find trips by destination (for search)
    List<Trip> findByDestinationContainingIgnoreCase(String destination);

    // Find public trips
    List<Trip> findByIsPublicTrue();
}
