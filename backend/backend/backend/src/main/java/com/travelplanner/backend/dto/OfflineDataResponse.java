package com.travelplanner.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfflineDataResponse {
    private String cacheKey;
    private String dataType;
    private Long dataId;
    private Object data; // The actual data (trip, itinerary, etc.)
    private Integer version;
    private LocalDateTime lastSynced;
    private LocalDateTime expiresAt;
    private boolean isDirty;
    private String deviceId;
    private LocalDateTime createdAt;
}
