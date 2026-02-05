package com.travelplanner.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryItemResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime itemDate;
    private String location;
    private String itemType;
    private Double cost;
    private String notes;
    private boolean isBooked;
    private String bookingReference;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long tripId;

    // Constructor from ItineraryItem entity
    public ItineraryItemResponse(com.travelplanner.backend.entity.ItineraryItem item) {
        this.id = item.getId();
        this.title = item.getTitle();
        this.description = item.getDescription();
        this.itemDate = item.getItemDate();
        this.location = item.getLocation();
        this.itemType = item.getItemType();
        this.cost = item.getCost();
        this.notes = item.getNotes();
        this.isBooked = item.isBooked();
        this.bookingReference = item.getBookingReference();
        this.createdAt = item.getCreatedAt();
        this.updatedAt = item.getUpdatedAt();
        this.tripId = item.getTrip().getId();
    }
}