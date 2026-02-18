package com.travelplanner.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {
    private Long totalUsers;
    private Long activeUsersLast30Days;
    private Long totalTrips;
    private Long totalBookings;
    private Long totalNotificationsSent;
    private Double totalBookingValue;
    private Map<String, Long> usersByType;
    private Map<String, Long> bookingsByType;
    private Map<String, Long> logsByActionType;
    private SystemHealth systemHealth;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemHealth {
        private String databaseStatus;
        private Long databaseSizeMB;
        private Integer activeConnections;
        private Double systemLoad;
        private Long freeMemoryMB;
        private Long totalMemoryMB;
        private String lastBackup;
    }
}
