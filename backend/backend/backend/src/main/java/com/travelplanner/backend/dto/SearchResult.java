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
    private String provider; // Amadeus, OpenWeather, Google, etc.
    private String externalId; // External API's ID for the item
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
    private Integer availableCount;

    // Flight specific fields
    private String airline;
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String duration;
    private Integer stops;
    private String cabinClass;

    // Hotel specific fields
    private String hotelChain;
    private String roomType;
    private String amenities;
    private Integer starRating;
    private String address;
    private String city;
    private String country;
    private String checkInTime;
    private String checkOutTime;

    // Activity specific fields
    private String activityType;
    private String durationHours;
    private String guideLanguage;
    private Integer maxParticipants;
    private String meetingPoint;
    private String includes;
    private String excludes;

    // Common fields
    private String imageUrl;
    private String cancellationPolicy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}