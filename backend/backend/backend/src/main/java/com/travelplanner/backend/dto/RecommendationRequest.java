package com.travelplanner.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequest {
    private String location;
    private String recommendationType; // ACTIVITY, RESTAURANT, POI
    private String category;
    private Integer maxPriceLevel;
    private Double minRating;
    private Integer limit = 10;
    private String sortBy = "rating"; // rating, popularity, price
    private String sortOrder = "desc";
}
