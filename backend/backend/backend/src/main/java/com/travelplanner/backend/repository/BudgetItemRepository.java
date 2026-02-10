package com.travelplanner.backend.repository;

import com.travelplanner.backend.entity.Trip;
import com.travelplanner.backend.entity.BudgetItem;
import com.travelplanner.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BudgetItemRepository extends JpaRepository<BudgetItem, Long> {

    // Find budget items by trip
    List<BudgetItem> findByTrip(Trip trip);

    // Find budget items by trip ID
    List<BudgetItem> findByTripId(Long tripId);

    // Find budget items by user
    List<BudgetItem> findByUser(User user);

    // Find budget items by category
    List<BudgetItem> findByCategory(String category);

    // Find paid budget items
    List<BudgetItem> findByIsPaidTrue();

    // Find unpaid budget items
    List<BudgetItem> findByIsPaidFalse();

    // Calculate total estimated amount for a trip
    @Query("SELECT COALESCE(SUM(b.estimatedAmount), 0) FROM BudgetItem b WHERE b.trip.id = :tripId")
    Double getTotalEstimatedAmountByTripId(@Param("tripId") Long tripId);

    // Calculate total actual amount for a trip
    @Query("SELECT COALESCE(SUM(b.actualAmount), 0) FROM BudgetItem b WHERE b.trip.id = :tripId AND b.actualAmount IS NOT NULL")
    Double getTotalActualAmountByTripId(@Param("tripId") Long tripId);

    // Calculate total amount by category for a trip
    @Query("SELECT b.category, COALESCE(SUM(b.estimatedAmount), 0) as estimated, COALESCE(SUM(b.actualAmount), 0) as actual " +
            "FROM BudgetItem b WHERE b.trip.id = :tripId GROUP BY b.category")
    List<Object[]> getAmountByCategory(@Param("tripId") Long tripId);

    // Find budget items by trip and category
    List<BudgetItem> findByTripAndCategory(Trip trip, String category);
}
