package com.travelplanner.backend.dto.external;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelBookingResponse {
    private String bookingReference;
    private String provider;
    private String hotelId;
    private String hotelName;
    private String status;           // CONFIRMED, PENDING, CANCELLED
    private BigDecimal totalPrice;
    private String currency;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private String roomType;
    private int rooms;
    private String cancellationNumber;
    private String cancellationPolicy;
    private LocalDateTime bookingTime;
    private String confirmationEmail;
}