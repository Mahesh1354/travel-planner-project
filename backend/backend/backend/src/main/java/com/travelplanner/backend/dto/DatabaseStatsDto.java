package com.travelplanner.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseStatsDto {
    private Long totalUsers;
    private Long totalTrips;
    private Long totalBookings;
    private Long databaseSizeMb;
    private String lastBackupTime;
    private Integer activeConnections;

    // Additional useful stats
    private Long totalItineraryItems;
    private Long totalNotifications;
    private Double averageBookingsPerUser;
}