package com.travelplanner.backend.dto.external;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelBookingRequest {
    private String hotelId;
    private String provider;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private List<Guest> guests;
    private int rooms;
    private String roomType;
    private String specialRequests;
    private String contactEmail;
    private String contactPhone;
    private String paymentMethod;
    private String cardNumber;      // Will be encrypted
    private String cardExpiry;
    private String cardCvv;         // Will be encrypted
    private String cardHolderName;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Guest {
        private String firstName;
        private String lastName;
        private String age;         // ADULT, CHILD
    }
}