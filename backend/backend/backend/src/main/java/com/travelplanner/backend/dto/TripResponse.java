package com.travelplanner.backend.dto;

import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.entity.Trip;
import com.travelplanner.backend.service.TripService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

public class TripResponse implements TripService {

    // =========================
    // Trip operations
    // =========================

    @Override
    public TripResponse createTrip(String userEmail, TripRequest tripRequest) {
        Trip trip = new Trip(); // later: save via repository
        return new TripResponse();
    }

    @Override
    public TripResponse getTripById(Long tripId, String userEmail) {
        Trip trip = new Trip(); // later: fetch from DB
        return new TripResponse();
    }

    @Override
    public List<TripResponse> getUserTrips(String userEmail) {
        return new ArrayList<>();
    }

    @Override
    public TripResponse updateTrip(Long tripId, String userEmail, TripRequest tripRequest) {
        Trip trip = new Trip();
        return new TripResponse();
    }

    @Override
    public void deleteTrip(Long tripId, String userEmail) {
        // later: delete from DB
    }

    @Override
    public TripResponse duplicateTrip(Long tripId, String userEmail) {
        Trip trip = new Trip();
        return new TripResponse();
    }

    // =========================
    // Itinerary item operations
    // =========================

    @Override
    public ItineraryItemResponse addItineraryItem(
            Long tripId,
            String userEmail,
            ItineraryItemRequest itemRequest
    ) {
        return new ItineraryItemResponse(null);
    }

    @Override
    public ItineraryItemResponse updateItineraryItem(
            Long itemId,
            String userEmail,
            ItineraryItemRequest itemRequest
    ) {
        return new ItineraryItemResponse(null);
    }

    @Override
    public void deleteItineraryItem(Long itemId, String userEmail) {
    }

    @Override
    public List<ItineraryItemResponse> getTripItineraryItems(Long tripId, String userEmail) {
        return new ArrayList<>();
    }

    // =========================
    // Collaborator operations
    // =========================

    @Override
    public CollaboratorResponse addCollaborator(
            Long tripId,
            String ownerEmail,
            CollaboratorRequest collaboratorRequest
    ) {
        return new CollaboratorResponse(null);
    }

    @Override
    public void removeCollaborator(
            Long tripId,
            String ownerEmail,
            String collaboratorEmail
    ) {
    }

    @Override
    public List<CollaboratorResponse> getTripCollaborators(Long tripId, String userEmail) {
        return new ArrayList<>();
    }

    @Override
    public void acceptCollaboration(Long tripId, String userEmail) {
    }
}
