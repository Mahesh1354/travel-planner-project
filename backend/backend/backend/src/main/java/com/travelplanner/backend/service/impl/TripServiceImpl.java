package com.travelplanner.backend.service.impl;

import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.service.TripService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TripServiceImpl implements TripService {

    // =========================
    // Trip operations
    // =========================

    @Override
    public TripResponse createTrip(String userEmail, TripRequest tripRequest) {
        return new TripResponse();
    }

    @Override
    public TripResponse getTripById(Long tripId, String userEmail) {
        return new TripResponse();
    }

    @Override
    public List<TripResponse> getUserTrips(String userEmail) {
        return new ArrayList<>();
    }

    @Override
    public TripResponse updateTrip(Long tripId, String userEmail, TripRequest tripRequest) {
        return new TripResponse();
    }

    @Override
    public void deleteTrip(Long tripId, String userEmail) {
        // TODO: implement delete logic
    }

    @Override
    public TripResponse duplicateTrip(Long tripId, String userEmail) {
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
        // TODO: implement delete logic
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
        // TODO: implement remove collaborator logic
    }

    @Override
    public List<CollaboratorResponse> getTripCollaborators(
            Long tripId,
            String userEmail
    ) {
        return new ArrayList<>();
    }

    @Override
    public void acceptCollaboration(Long tripId, String userEmail) {
        // TODO: implement accept collaboration logic
    }
}
