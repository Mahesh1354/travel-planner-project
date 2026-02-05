package com.travelplanner.backend.service;

import com.travelplanner.backend.dto.*;

import java.util.List;

public interface TripService {

    // Trip operations
    TripResponse createTrip(String userEmail, TripRequest tripRequest);
    TripResponse getTripById(Long tripId, String userEmail);
    List<TripResponse> getUserTrips(String userEmail);
    TripResponse updateTrip(Long tripId, String userEmail, TripRequest tripRequest);
    void deleteTrip(Long tripId, String userEmail);
    TripResponse duplicateTrip(Long tripId, String userEmail);

    // Itinerary item operations
    ItineraryItemResponse addItineraryItem(Long tripId, String userEmail, ItineraryItemRequest itemRequest);
    ItineraryItemResponse updateItineraryItem(Long itemId, String userEmail, ItineraryItemRequest itemRequest);
    void deleteItineraryItem(Long itemId, String userEmail);
    List<ItineraryItemResponse> getTripItineraryItems(Long tripId, String userEmail);

    // Collaborator operations
    CollaboratorResponse addCollaborator(Long tripId, String ownerEmail, CollaboratorRequest collaboratorRequest);
    void removeCollaborator(Long tripId, String ownerEmail, String collaboratorEmail);
    List<CollaboratorResponse> getTripCollaborators(Long tripId, String userEmail);
    void acceptCollaboration(Long tripId, String userEmail);
}
