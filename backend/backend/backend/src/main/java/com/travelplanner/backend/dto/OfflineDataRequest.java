package com.travelplanner.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfflineDataRequest {
    private String dataType; // TRIP, ITINERARY, BOOKING, RECOMMENDATION
    private Long dataId; // Optional: specific ID to cache
    private String deviceId; // Unique device identifier
    private Integer expirationDays = 7; // How long to keep cache
    private boolean includeRelated = true; // Include related data (e.g., trip with itinerary items)
}
