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
public class HotelResponse {
    private String provider;           // "amadeus", "expedia", "booking"
    private String hotelId;
    private String hotelName;
    private String hotelChain;
    private String description;
    private String address;
    private String city;
    private String country;
    private String postalCode;
    private Double latitude;
    private Double longitude;
    private Integer starRating;
    private Double userRating;
    private Integer reviewCount;
    private BigDecimal pricePerNight;
    private BigDecimal totalPrice;
    private String currency;
    private String roomType;
    private Integer availableRooms;
    private List<String> amenities;
    private List<String> images;
    private String checkInTime;
    private String checkOutTime;
    private String cancellationPolicy;
    private LocalDateTime availabilityExpires;

    // ✅ ADD THIS INNER CLASS
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomOffer {
        private String roomType;
        private String bedType;
        private Integer maxGuests;
        private BigDecimal pricePerNight;
        private BigDecimal totalPrice;
        private String currency;
        private Integer availableRooms;
        private List<String> amenities;
        private String cancellationPolicy;
        private Boolean breakfastIncluded;
        private Boolean refundable;
    }
}