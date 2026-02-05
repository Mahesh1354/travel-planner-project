package com.travelplanner.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripResponse {
    private Long id;
    private String tripName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String destination;
    private Double budget;
    private String notes;
    private boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponse user;
    private List<ItineraryItemResponse> itineraryItems;
    private List<CollaboratorResponse> collaborators;

    // Constructor from Trip entity
    public TripResponse(com.travelplanner.backend.entity.Trip trip) {
        this.id = trip.getId();
        this.tripName = trip.getTripName();
        this.description = trip.getDescription();
        this.startDate = trip.getStartDate();
        this.endDate = trip.getEndDate();
        this.destination = trip.getDestination();
        this.budget = trip.getBudget();
        this.notes = trip.getNotes();
        this.isPublic = trip.isPublic();
        this.createdAt = trip.getCreatedAt();
        this.updatedAt = trip.getUpdatedAt();
        this.user = new UserResponse(trip.getUser());
    }
}