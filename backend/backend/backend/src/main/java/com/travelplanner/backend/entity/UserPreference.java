package com.travelplanner.backend.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_preferences",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "preference_type", "preference_key"})
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "preference_type", nullable = false)
    private String preferenceType; // RECOMMENDATION, ACTIVITY, FOOD, BUDGET

    @Column(name = "preference_key", nullable = false)
    private String preferenceKey; // cuisine_type, activity_level, budget_range

    @Column(name = "preference_value", nullable = false)
    private String preferenceValue; // italian, adventurous, medium

    @Column(name = "weight")
    private Double weight = 1.0; // 0.0-1.0, importance weight for recommendations

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
