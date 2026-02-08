package com.travelplanner.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncRequest {
    private String deviceId;
    private List<CacheItem> cacheItems;
    private List<OfflineChange> offlineChanges;
    private boolean fullSync = false;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CacheItem {
        private String cacheKey;
        private String dataType;
        private Long dataId;
        private Integer version;
        private String data; // JSON string
        private boolean isDirty;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OfflineChange {
        private String changeType; // CREATE, UPDATE, DELETE
        private String entityType; // TRIP, ITINERARY_ITEM, BOOKING, etc.
        private Long entityId; // null for CREATE
        private Map<String, Object> data; // Changed data
        private LocalDateTime changeTimestamp;
        private String changeId; // Unique ID for this change
    }
}
