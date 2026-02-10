package com.travelplanner.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {
    private Long id;
    private String recommendationType;
    private String title;
    private String description;
    private String category;
    private String location;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double rating;
    private Integer priceLevel;
    private String openingHours;
    private String website;
    private String phone;
    private String tags;
    private boolean isPopular;
    private boolean isSeasonal;
    private Double relevanceScore; // How relevant this is to user preferences
    private LocalDateTime createdAt;

    // Constructor from Recommendation entity
    public RecommendationResponse(com.travelplanner.backend.entity.Recommendation recommendation) {
        this.id = recommendation.getId();
        this.recommendationType = recommendation.getRecommendationType();
        this.title = recommendation.getTitle();
        this.description = recommendation.getDescription();
        this.category = recommendation.getCategory();
        this.location = recommendation.getLocation();
        this.address = recommendation.getAddress();
        this.latitude = recommendation.getLatitude();
        this.longitude = recommendation.getLongitude();
        this.rating = recommendation.getRating();
        this.priceLevel = recommendation.getPriceLevel();
        this.openingHours = recommendation.getOpeningHours();
        this.website = recommendation.getWebsite();
        this.phone = recommendation.getPhone();
        this.tags = recommendation.getTags();
        this.isPopular = recommendation.isPopular();
        this.isSeasonal = recommendation.isSeasonal();
        this.createdAt = recommendation.getCreatedAt();
    }
}
