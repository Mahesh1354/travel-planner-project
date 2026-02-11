package com.travelplanner.backend.controller;



import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.service.JwtService;
import com.travelplanner.backend.service.TripService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    @Autowired
    private JwtService jwtService;

    // Helper method to extract user email from JWT token
    private String getCurrentUserEmail(String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Remove "Bearer "
        return jwtService.getEmailFromToken(token);
    }

    // Trip CRUD operations
    @PostMapping
    public ResponseEntity<Object> createTrip(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody TripRequest tripRequest) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        TripResponse tripResponse = tripService.createTrip(userEmail, tripRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Trip created successfully", tripResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getUserTrips(
            @RequestHeader("Authorization") String authorizationHeader) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        List<TripResponse> trips = tripService.getUserTrips(userEmail);

        return ResponseEntity.ok(ApiResponse.success("Trips retrieved successfully", trips));
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<ApiResponse> getTripById(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tripId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        TripResponse tripResponse = tripService.getTripById(tripId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Trip retrieved successfully", tripResponse));
    }

    @PutMapping("/{tripId}")
    public ResponseEntity<ApiResponse> updateTrip(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tripId,
            @Valid @RequestBody TripRequest tripRequest) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        TripResponse tripResponse = tripService.updateTrip(tripId, userEmail, tripRequest);

        return ResponseEntity.ok(ApiResponse.success("Trip updated successfully", tripResponse));
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<ApiResponse> deleteTrip(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tripId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        tripService.deleteTrip(tripId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Trip deleted successfully", null));
    }

    @PostMapping("/{tripId}/duplicate")
    public ResponseEntity<ApiResponse> duplicateTrip(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tripId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        TripResponse tripResponse = tripService.duplicateTrip(tripId, userEmail);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Trip duplicated successfully", tripResponse));
    }

    // Itinerary Item operations
    @PostMapping("/{tripId}/items")
    public ResponseEntity<ApiResponse> addItineraryItem(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tripId,
            @Valid @RequestBody ItineraryItemRequest itemRequest) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        ItineraryItemResponse itemResponse = tripService.addItineraryItem(tripId, userEmail, itemRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Itinerary item added successfully", itemResponse));
    }

    @GetMapping("/{tripId}/items")
    public ResponseEntity<ApiResponse> getTripItineraryItems(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tripId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        List<ItineraryItemResponse> items = tripService.getTripItineraryItems(tripId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Itinerary items retrieved successfully", items));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse> updateItineraryItem(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long itemId,
            @Valid @RequestBody ItineraryItemRequest itemRequest) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        ItineraryItemResponse itemResponse = tripService.updateItineraryItem(itemId, userEmail, itemRequest);

        return ResponseEntity.ok(ApiResponse.success("Itinerary item updated successfully", itemResponse));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse> deleteItineraryItem(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long itemId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        tripService.deleteItineraryItem(itemId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Itinerary item deleted successfully", null));
    }

    // Collaborator operations
    @PostMapping("/{tripId}/collaborators")
    public ResponseEntity<ApiResponse> addCollaborator(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tripId,
            @Valid @RequestBody CollaboratorRequest collaboratorRequest) {

        String ownerEmail = getCurrentUserEmail(authorizationHeader);
        CollaboratorResponse collaboratorResponse = tripService.addCollaborator(tripId, ownerEmail, collaboratorRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Collaborator added successfully", collaboratorResponse));
    }

    @GetMapping("/{tripId}/collaborators")
    public ResponseEntity<ApiResponse> getTripCollaborators(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tripId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        List<CollaboratorResponse> collaborators = tripService.getTripCollaborators(tripId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Collaborators retrieved successfully", collaborators));
    }

    @DeleteMapping("/{tripId}/collaborators/{collaboratorEmail}")
    public ResponseEntity<ApiResponse> removeCollaborator(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tripId,
            @PathVariable String collaboratorEmail) {

        String ownerEmail = getCurrentUserEmail(authorizationHeader);
        tripService.removeCollaborator(tripId, ownerEmail, collaboratorEmail);

        return ResponseEntity.ok(ApiResponse.success("Collaborator removed successfully", null));
    }

    @PostMapping("/{tripId}/collaborators/accept")
    public ResponseEntity<ApiResponse> acceptCollaboration(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tripId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        tripService.acceptCollaboration(tripId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Collaboration accepted successfully", null));
    }
}