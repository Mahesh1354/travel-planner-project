package com.travelplanner.backend.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "offline_cache")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfflineCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cache_key", nullable = false, length = 500)
    private String cacheKey; // Unique identifier for cached data

    @Column(name = "data_type", nullable = false)
    private String dataType; // TRIP, ITINERARY, BOOKING, RECOMMENDATION

    @Column(name = "data_id")
    private Long dataId; // ID of the data being cached

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Lob
    @Column(name = "cached_data", nullable = false, columnDefinition = "LONGTEXT")
    private String cachedData; // JSON string of the data

    @Column(name = "version")
    private Integer version = 1;

    @Column(name = "last_synced")
    private LocalDateTime lastSynced;

    @Column(name = "is_dirty")
    private boolean isDirty = false; // True if modified offline

    @Lob
    @Column(name = "offline_changes", columnDefinition = "LONGTEXT")
    private String offlineChanges;

    @Column(name = "device_id")
    private String deviceId; // ID of device that created cache

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Pre-persist and pre-update methods
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (expiresAt == null) {
            expiresAt = LocalDateTime.now().plusDays(7); // Default 7 days expiry
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method to check if cache is expired
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    // Helper method to check if cache needs sync
    public boolean needsSync() {
        return isDirty || isExpired();
    }
}
