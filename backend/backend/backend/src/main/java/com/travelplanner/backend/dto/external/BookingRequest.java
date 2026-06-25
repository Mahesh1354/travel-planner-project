package com.travelplanner.backend.dto.external;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    private String flightId;
    private String provider;
    private List<Passenger> passengers;
    private String contactEmail;
    private String contactPhone;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Passenger {
        private String firstName;
        private String lastName;
        private String dateOfBirth;
        private String gender;
        private String passportNumber;
        private String nationality;
        private String seatPreference; // WINDOW, AISLE, etc.
    }
}