package com.travelplanner.backend.repository;
import com.travelplanner.backend.entity.TravelTip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TravelTipRepository extends JpaRepository<TravelTip, Long> {

    // Find tips by country
    List<TravelTip> findByCountry(String country);

    // Find tips by country and city
    List<TravelTip> findByCountryAndCity(String country, String city);

    // Find tips by type
    List<TravelTip> findByTipType(String tipType);

    // Find active alerts (not expired)
    List<TravelTip> findByIsAlertTrueAndAlertExpiryAfter(java.time.LocalDateTime now);

    // Find tips by priority level
    List<TravelTip> findByCountryAndPriorityLevel(String country, Integer priorityLevel);

    // Find tips by country and type
    List<TravelTip> findByCountryAndTipType(String country, String tipType);
}