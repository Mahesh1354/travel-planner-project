package com.travelplanner.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    @NotBlank(message = "Search type is required")
    private String searchType; // FLIGHT, HOTEL, ACTIVITY

    @NotBlank(message = "Location is required")
    private String location; // Destination city/airport code

    private String origin; // For flights only

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate; // For hotels and multi-day activities

    private Integer adults = 1;

    private Integer children = 0;

    private Integer rooms = 1; // For hotels

    private String sortBy = "price"; // price, rating, duration

    private String sortOrder = "asc"; // asc, desc

    private Map<String, String> filters; // Additional filters

    // Flight specific
    private String cabinClass; // ECONOMY, PREMIUM_ECONOMY, BUSINESS, FIRST

    private Boolean nonStop = false;

    // Hotel specific
    private Integer minRating;

    private Integer maxPrice;

    // ✅ ADD THESE MISSING FIELDS FOR HOTEL SEARCH
    private String hotelName;           // Specific hotel name to search

    private String amenities;          // Comma-separated amenities (WI-FI, POOL, PARKING)

    private Integer limit = 20;        // Max results to return

    private String currency = "USD";   // Currency for pricing

    private String checkInTime;        // Preferred check-in time

    private String checkOutTime;       // Preferred check-out time

    // Activity specific
    private String activityType; // adventure, cultural, food, etc.

    private Integer duration;   // Activity duration in hours

    private String language;    // Guide language

    private Integer maxParticipants; // Max group size
}