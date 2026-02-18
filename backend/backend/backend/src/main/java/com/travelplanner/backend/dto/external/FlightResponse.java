package com.travelplanner.backend.dto.external;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightResponse {
    private String provider;           // "amadeus", "expedia", etc.
    private String flightId;
    private String airline;
    private String flightNumber;
    private String originCode;
    private String destinationCode;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int durationMinutes;
    private BigDecimal price;
    private String currency;
    private String cabinClass;
    private int availableSeats;
    private List<String> amenities;
    private List<Segment> segments;    // for connecting flights

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Segment {
        private String airline;
        private String flightNumber;
        private String originCode;
        private String destinationCode;
        private LocalDateTime departureTime;
        private LocalDateTime arrivalTime;
        private int durationMinutes;
    }
}