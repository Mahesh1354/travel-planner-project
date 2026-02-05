package com.travelplanner.backend.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryItemRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Item date is required")
    private LocalDateTime itemDate;

    private String location;

    private String itemType; // FLIGHT, ACCOMMODATION, ACTIVITY, FOOD, TRANSPORT, OTHER

    private Double cost;

    private String notes;

    private boolean isBooked = false;

    private String bookingReference;
}