package com.travelplanner.backend.dto;

import com.travelplanner.backend.entity.Trip;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    // User information
    private UserResponse user;

    // Collections
    private List<ItineraryItemResponse> itineraryItems = new ArrayList<>();
    private List<CollaboratorResponse> collaborators = new ArrayList<>();

    // Constructor from Trip entity
    public TripResponse(Trip trip) {
        this.id = trip.getId();
        this.tripName = trip.getTripName();
        this.description = trip.getDescription();
        this.startDate = trip.getStartDate();
        this.endDate = trip.getEndDate();
        this.destination = trip.getDestination();
        this.budget = trip.getBudget();
        this.notes = trip.getNotes();
        this.isPublic = trip.isPublicTrip();
        this.createdAt = trip.getCreatedAt();
        this.updatedAt = trip.getUpdatedAt();

        if (trip.getUser() != null) {
            this.user = new UserResponse(trip.getUser());
        }
    }

    // Helper methods to add items (optional)
    public void addItineraryItem(ItineraryItemResponse item) {
        this.itineraryItems.add(item);
    }

    public void addCollaborator(CollaboratorResponse collaborator) {
        this.collaborators.add(collaborator);
    }
}