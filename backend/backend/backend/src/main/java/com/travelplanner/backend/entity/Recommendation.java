package com.travelplanner.backend.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recommendation_type", nullable = false)
    private String recommendationType; // ACTIVITY, RESTAURANT, POI, TIP

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "category")
    private String category; // adventure, cultural, food, nature, shopping, etc.

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "address")
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "price_level")
    private Integer priceLevel; // 1-4 (1=cheap, 4=expensive)

    @Column(name = "opening_hours")
    private String openingHours;

    @Column(name = "website")
    private String website;

    @Column(name = "phone")
    private String phone;

    @Column(name = "tags")
    private String tags; // comma-separated tags

    @Column(name = "is_popular")
    private boolean isPopular = false;

    @Column(name = "is_seasonal")
    private boolean isSeasonal = false;

    @Column(name = "season_start")
    private String seasonStart; // MM-DD format

    @Column(name = "season_end")
    private String seasonEnd; // MM-DD format

    @Column(name = "source")
    private String source = "SYSTEM"; // SYSTEM, USER, EXTERNAL_API

    @Column(name = "external_id")
    private String externalId; // ID from external API if applicable

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip; // Optional: if recommendation is for a specific trip

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Optional: if user saved this recommendation

    // Pre-persist and pre-update methods
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}