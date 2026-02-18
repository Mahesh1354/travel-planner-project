package com.travelplanner.backend.repository;

import com.travelplanner.backend.entity.ItineraryItem;
import com.travelplanner.backend.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ItineraryItemRepository extends JpaRepository<ItineraryItem, Long> {

    // Find all items for a trip
    List<ItineraryItem> findByTrip(Trip trip);

    // Find items for a trip ordered by date
    List<ItineraryItem> findByTripOrderByItemDateAsc(Trip trip);

    // Delete all items for a trip - ADD THIS METHOD
    @Transactional
    @Modifying
    @Query("DELETE FROM ItineraryItem ii WHERE ii.trip = :trip")
    void deleteByTrip(@Param("trip") Trip trip);

    // Alternative simpler method
    // void deleteByTrip(Trip trip);
}