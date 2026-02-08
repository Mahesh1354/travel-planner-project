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
public class SyncResponse {
    private boolean success;
    private String message;
    private List<SyncItem> updates;
    private List<ConflictItem> conflicts;
    private String syncToken;
    private LocalDateTime serverTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SyncItem {
        private String cacheKey;
        private String dataType;
        private Long dataId;
        private Integer version;
        private String data; // JSON string
        private String action; // UPDATE, DELETE, NO_CHANGE
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConflictItem {
        private String cacheKey;
        private String dataType;
        private Long dataId;
        private String clientData; // Client's version
        private String serverData; // Server's version
        private String resolution; // CLIENT_WINS, SERVER_WINS, MANUAL
    }
}
