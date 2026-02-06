package com.travelplanner.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    private String id;
    private String searchType; // FLIGHT, HOTEL, ACTIVITY
    private String provider; // MockAir, MockHotels, MockActivities
    private String name;
    private String description;
    private String location;
    private Double price;
    private String currency = "USD";
    private Double rating;
    private Integer reviewCount;
    private Map<String, Object> details;
    private LocalDateTime availableFrom;
    private LocalDateTime availableTo;
    private Boolean isAvailable = true;

    // Flight specific
    private String airline;
    private String flightNumber;
    private String origin; // ADD THIS FIELD
    private String destination; // Already exists
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String duration;
    private Integer stops;

    // Hotel specific
    private String hotelChain;
    private String roomType;
    private String amenities;
    private Integer starRating;

    // Activity specific
    private String activityType;
    private String durationHours;
    private String guideLanguage;
    private Integer maxParticipants;
}