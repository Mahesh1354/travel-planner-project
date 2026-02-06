package com.travelplanner.backend.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @NotNull(message = "Trip ID is required")
    private Long tripId;

    @NotBlank(message = "Booking type is required")
    private String bookingType; // FLIGHT, HOTEL, ACTIVITY

    @NotBlank(message = "Item ID is required")
    private String itemId; // ID from search result

    @NotBlank(message = "Item name is required")
    private String itemName;

    private String description;

    @NotNull(message = "Price is required")
    private Double price;

    private String currency = "USD";

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String location;

    private String providerName = "MockProvider";

    private Map<String, Object> bookingDetails;

    private String cancellationPolicy;

    // Link to existing itinerary item (optional)
    private Long itineraryItemId;
}
