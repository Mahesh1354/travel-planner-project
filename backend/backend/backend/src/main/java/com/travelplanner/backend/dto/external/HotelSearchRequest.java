package com.travelplanner.backend.dto.external;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelSearchRequest {
    private String cityCode;        // e.g., "NYC", "LON", "PAR"
    private String hotelName;       // Optional: specific hotel
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int adults;
    private int rooms;             // default 1
    private String roomType;       // SINGLE, DOUBLE, SUITE
    private Integer minRating;     // 1-5 star rating
    private Double maxPrice;
    private String currency;       // USD, EUR, INR
    private String amenities;      // WIFI, POOL, PARKING, etc.
    private int maxResults;        // default 20
    private String sortBy;         // PRICE, RATING, DISTANCE
}