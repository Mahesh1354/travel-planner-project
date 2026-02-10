package com.travelplanner.backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MockFlightResponse {
    private boolean success;
    private String message;
    private List<MockFlight> flights;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MockFlight {
        private String id;
        private String airline;
        private String flightNumber;
        private String origin;
        private String destination;
        private LocalDateTime departureTime;
        private LocalDateTime arrivalTime;
        private String duration;
        private String cabinClass;
        private Double price;
        private String currency;
        private Integer availableSeats;
        private Integer stops;
        private String aircraftType;
        private String baggageAllowance;
        private Boolean refundable;
        private Double rating;
    }
}