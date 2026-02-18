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
public class FlightSearchRequest {
    private String originCode;        // e.g., "JFK"
    private String destinationCode;   // e.g., "LAX"
    private LocalDate departureDate;
    private LocalDate returnDate;     // null for one-way
    private int adults;
    private int children;            // default 0
    private String cabinClass;       // ECONOMY, BUSINESS, FIRST
    private String currency;         // USD, EUR, INR
    private int maxResults;          // default 10
}