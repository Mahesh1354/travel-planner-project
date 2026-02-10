package com.travelplanner.backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MockHotelResponse {
    private boolean success;
    private String message;
    private List<MockHotel> hotels;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MockHotel {
        private String id;
        private String name;
        private String chain;
        private String location;
        private String address;
        private Double pricePerNight;
        private String currency;
        private Integer starRating;
        private Double guestRating;
        private Integer reviewCount;
        private String roomType;
        private String amenities;
        private Boolean freeCancellation;
        private Boolean breakfastIncluded;
        private Boolean wifiIncluded;
        private Integer availableRooms;
    }
}
