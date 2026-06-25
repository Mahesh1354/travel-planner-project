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
public class BookingResponse {
    private String bookingReference;
    private String provider;
    private String flightId;
    private String status;           // CONFIRMED, PENDING, CANCELLED
    private BigDecimal totalPrice;
    private String currency;
    private LocalDateTime bookingTime;
    private String ticketNumber;
    private String eticketUrl;
}