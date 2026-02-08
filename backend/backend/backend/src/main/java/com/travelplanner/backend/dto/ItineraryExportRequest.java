package com.travelplanner.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryExportRequest {
    private Long tripId;
    private String format = "JSON"; // JSON, PDF, CSV
    private boolean includeBookings = true;
    private boolean includeBudget = true;
    private boolean includeRecommendations = false;
    private String password; // Optional: for encrypted exports
}
