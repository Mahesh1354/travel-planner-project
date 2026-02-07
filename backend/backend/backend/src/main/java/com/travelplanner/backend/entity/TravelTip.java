package com.travelplanner.backend.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "travel_tips")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelTip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tip_type", nullable = false)
    private String tipType; // VISA, SAFETY, CULTURE, MONEY, HEALTH, TRANSPORT

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", length = 3000, nullable = false)
    private String content;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "city")
    private String city; // Optional: city-specific tips

    @Column(name = "priority_level")
    private Integer priorityLevel = 1; // 1-3 (1=important, 3=good to know)

    @Column(name = "is_alert")
    private boolean isAlert = false; // For urgent safety alerts

    @Column(name = "alert_expiry")
    private LocalDateTime alertExpiry; // When alert expires

    @Column(name = "source")
    private String source = "SYSTEM"; // SYSTEM, GOVT, USER

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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
