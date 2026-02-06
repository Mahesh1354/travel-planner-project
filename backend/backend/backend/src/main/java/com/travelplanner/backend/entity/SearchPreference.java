package com.travelplanner.backend.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "search_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "preference_type", nullable = false)
    private String preferenceType; // FLIGHT, HOTEL, ACTIVITY

    @Column(name = "preference_key", nullable = false)
    private String preferenceKey; // airline_class, hotel_rating, activity_type

    @Column(name = "preference_value", nullable = false)
    private String preferenceValue; // business, 4, adventure

    @Column(name = "priority")
    private Integer priority = 1; // 1-5, higher is more important

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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