package com.travelplanner.backend.repository;


import com.travelplanner.backend.entity.ItineraryItem;
import com.travelplanner.backend.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItineraryItemRepository extends JpaRepository<ItineraryItem, Long> {

    // Find items by trip
    List<ItineraryItem> findByTrip(Trip trip);

    // Find items by trip ID
    List<ItineraryItem> findByTripId(Long tripId);

    // Find items by date range
    List<ItineraryItem> findByItemDateBetween(LocalDateTime start, LocalDateTime end);

    // Find items by type
    List<ItineraryItem> findByItemType(String itemType);
}