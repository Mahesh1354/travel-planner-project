package com.travelplanner.backend.repository;
import com.travelplanner.backend.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    // Find recommendations by location
    List<Recommendation> findByLocationContainingIgnoreCase(String location);

    // Find recommendations by type
    List<Recommendation> findByRecommendationType(String recommendationType);

    // Find recommendations by category
    List<Recommendation> findByCategory(String category);

    // Find popular recommendations
    List<Recommendation> findByIsPopularTrue();

    // Find recommendations by location and type
    List<Recommendation> findByLocationContainingIgnoreCaseAndRecommendationType(String location, String type);

    // Find recommendations by location and category
    List<Recommendation> findByLocationContainingIgnoreCaseAndCategory(String location, String category);

    // Find recommendations within price range
    @Query("SELECT r FROM Recommendation r WHERE r.location LIKE %:location% AND r.priceLevel BETWEEN :minPrice AND :maxPrice")
    List<Recommendation> findByLocationAndPriceRange(
            @Param("location") String location,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice);

    // Find recommendations with minimum rating
    List<Recommendation> findByLocationContainingIgnoreCaseAndRatingGreaterThanEqual(String location, Double minRating);

    // Find seasonal recommendations for current date
    @Query("SELECT r FROM Recommendation r WHERE r.isSeasonal = true AND " +
            "(:currentDate BETWEEN CONCAT('2024-', r.seasonStart) AND CONCAT('2024-', r.seasonEnd))")
    List<Recommendation> findSeasonalRecommendations(@Param("currentDate") String currentDate);
}
