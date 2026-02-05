package com.travelplanner.backend.service.impl;


import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.entity.*;
import com.travelplanner.backend.exception.ResourceNotFoundException;
import com.travelplanner.backend.exception.UnauthorizedAccessException;
import com.travelplanner.backend.repository.*;
import com.travelplanner.backend.service.TripService;
import com.travelplanner.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripServiceImpl implements TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ItineraryItemRepository itineraryItemRepository;

    @Autowired
    private TripCollaboratorRepository tripCollaboratorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public TripResponse createTrip(String userEmail, TripRequest tripRequest) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate dates
        if (tripRequest.getStartDate().isAfter(tripRequest.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        Trip trip = new Trip();
        trip.setTripName(tripRequest.getTripName());
        trip.setDescription(tripRequest.getDescription());
        trip.setStartDate(tripRequest.getStartDate());
        trip.setEndDate(tripRequest.getEndDate());
        trip.setDestination(tripRequest.getDestination());
        trip.setBudget(tripRequest.getBudget());
        trip.setNotes(tripRequest.getNotes());
        trip.setPublic(tripRequest.isPublic());
        trip.setUser(user);

        Trip savedTrip = tripRepository.save(trip);
        return new TripResponse(savedTrip);
    }

    @Override
    public TripResponse getTripById(Long tripId, String userEmail) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        // Check if user has access
        if (!hasAccessToTrip(trip, userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to view this trip");
        }

        return createCompleteTripResponse(trip);
    }

    @Override
    public List<TripResponse> getUserTrips(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get trips owned by user
        List<Trip> ownedTrips = tripRepository.findByUser(user);

        // Get trips where user is collaborator
        List<TripCollaborator> collaborations = tripCollaboratorRepository.findByUser(user);
        List<Trip> collaboratedTrips = collaborations.stream()
                .map(TripCollaborator::getTrip)
                .collect(Collectors.toList());

        // Combine and remove duplicates
        ownedTrips.addAll(collaboratedTrips);

        return ownedTrips.stream()
                .map(this::createCompleteTripResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TripResponse updateTrip(Long tripId, String userEmail, TripRequest tripRequest) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        // Check if user has edit permission
        if (!hasEditPermission(trip, userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to edit this trip");
        }

        // Validate dates
        if (tripRequest.getStartDate().isAfter(tripRequest.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        trip.setTripName(tripRequest.getTripName());
        trip.setDescription(tripRequest.getDescription());
        trip.setStartDate(tripRequest.getStartDate());
        trip.setEndDate(tripRequest.getEndDate());
        trip.setDestination(tripRequest.getDestination());
        trip.setBudget(tripRequest.getBudget());
        trip.setNotes(tripRequest.getNotes());
        trip.setPublic(tripRequest.isPublic());

        Trip updatedTrip = tripRepository.save(trip);
        return createCompleteTripResponse(updatedTrip);
    }

    @Override
    @Transactional
    public void deleteTrip(Long tripId, String userEmail) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        // Check if user is the owner
        if (!trip.getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedAccessException("Only trip owner can delete the trip");
        }

        tripRepository.delete(trip);
    }

    @Override
    @Transactional
    public TripResponse duplicateTrip(Long tripId, String userEmail) {
        Trip originalTrip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        // Check if user has access to original trip
        if (!hasAccessToTrip(originalTrip, userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to duplicate this trip");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Create new trip with "Copy of" prefix
        Trip duplicateTrip = new Trip();
        duplicateTrip.setTripName("Copy of " + originalTrip.getTripName());
        duplicateTrip.setDescription(originalTrip.getDescription());
        duplicateTrip.setStartDate(originalTrip.getStartDate());
        duplicateTrip.setEndDate(originalTrip.getEndDate());
        duplicateTrip.setDestination(originalTrip.getDestination());
        duplicateTrip.setBudget(originalTrip.getBudget());
        duplicateTrip.setNotes(originalTrip.getNotes());
        duplicateTrip.setPublic(false); // Duplicates are private by default
        duplicateTrip.setUser(user);

        Trip savedTrip = tripRepository.save(duplicateTrip);

        // Duplicate itinerary items
        for (ItineraryItem originalItem : originalTrip.getItineraryItems()) {
            ItineraryItem duplicateItem = new ItineraryItem();
            duplicateItem.setTitle(originalItem.getTitle());
            duplicateItem.setDescription(originalItem.getDescription());
            duplicateItem.setItemDate(originalItem.getItemDate());
            duplicateItem.setLocation(originalItem.getLocation());
            duplicateItem.setItemType(originalItem.getItemType());
            duplicateItem.setCost(originalItem.getCost());
            duplicateItem.setNotes(originalItem.getNotes());
            duplicateItem.setBooked(false); // Reset booking status
            duplicateItem.setBookingReference(null); // Clear booking reference
            duplicateItem.setTrip(savedTrip);

            itineraryItemRepository.save(duplicateItem);
        }

        return createCompleteTripResponse(savedTrip);
    }

    @Override
    @Transactional
    public ItineraryItemResponse addItineraryItem(Long tripId, String userEmail, ItineraryItemRequest itemRequest) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        // Check if user has edit permission
        if (!hasEditPermission(trip, userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to add items to this trip");
        }

        ItineraryItem item = new ItineraryItem();
        item.setTitle(itemRequest.getTitle());
        item.setDescription(itemRequest.getDescription());
        item.setItemDate(itemRequest.getItemDate());
        item.setLocation(itemRequest.getLocation());
        item.setItemType(itemRequest.getItemType());
        item.setCost(itemRequest.getCost());
        item.setNotes(itemRequest.getNotes());
        item.setBooked(itemRequest.isBooked());
        item.setBookingReference(itemRequest.getBookingReference());
        item.setTrip(trip);

        ItineraryItem savedItem = itineraryItemRepository.save(item);
        return new ItineraryItemResponse(savedItem);
    }

    @Override
    @Transactional
    public ItineraryItemResponse updateItineraryItem(Long itemId, String userEmail, ItineraryItemRequest itemRequest) {
        ItineraryItem item = itineraryItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Itinerary item not found with id: " + itemId));

        // Check if user has edit permission for the trip
        if (!hasEditPermission(item.getTrip(), userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to update this item");
        }

        item.setTitle(itemRequest.getTitle());
        item.setDescription(itemRequest.getDescription());
        item.setItemDate(itemRequest.getItemDate());
        item.setLocation(itemRequest.getLocation());
        item.setItemType(itemRequest.getItemType());
        item.setCost(itemRequest.getCost());
        item.setNotes(itemRequest.getNotes());
        item.setBooked(itemRequest.isBooked());
        item.setBookingReference(itemRequest.getBookingReference());

        ItineraryItem updatedItem = itineraryItemRepository.save(item);
        return new ItineraryItemResponse(updatedItem);
    }

    @Override
    @Transactional
    public void deleteItineraryItem(Long itemId, String userEmail) {
        ItineraryItem item = itineraryItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Itinerary item not found with id: " + itemId));

        // Check if user has edit permission for the trip
        if (!hasEditPermission(item.getTrip(), userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to delete this item");
        }

        itineraryItemRepository.delete(item);
    }

    @Override
    public List<ItineraryItemResponse> getTripItineraryItems(Long tripId, String userEmail) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        // Check if user has access to trip
        if (!hasAccessToTrip(trip, userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to view this trip's items");
        }

        return itineraryItemRepository.findByTrip(trip).stream()
                .map(ItineraryItemResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CollaboratorResponse addCollaborator(Long tripId, String ownerEmail, CollaboratorRequest collaboratorRequest) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        // Check if requester is trip owner
        if (!trip.getUser().getEmail().equals(ownerEmail)) {
            throw new UnauthorizedAccessException("Only trip owner can add collaborators");
        }

        // Find collaborator user by email
        User collaboratorUser = userRepository.findByEmail(collaboratorRequest.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + collaboratorRequest.getUserEmail()));

        // Check if not adding yourself
        if (collaboratorUser.getEmail().equals(ownerEmail)) {
            throw new IllegalArgumentException("Cannot add yourself as collaborator");
        }

        // Check if already a collaborator
        if (tripCollaboratorRepository.existsByTripAndUser(trip, collaboratorUser)) {
            throw new IllegalArgumentException("User is already a collaborator on this trip");
        }

        TripCollaborator collaborator = new TripCollaborator();
        collaborator.setTrip(trip);
        collaborator.setUser(collaboratorUser);
        collaborator.setPermissionLevel(
                TripCollaborator.PermissionLevel.valueOf(collaboratorRequest.getPermissionLevel()));
        collaborator.setAccepted(false);

        TripCollaborator savedCollaborator = tripCollaboratorRepository.save(collaborator);
        return new CollaboratorResponse(savedCollaborator);
    }

    @Override
    @Transactional
    public void removeCollaborator(Long tripId, String ownerEmail, String collaboratorEmail) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        // Check if requester is trip owner
        if (!trip.getUser().getEmail().equals(ownerEmail)) {
            throw new UnauthorizedAccessException("Only trip owner can remove collaborators");
        }

        User collaboratorUser = userRepository.findByEmail(collaboratorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + collaboratorEmail));

        TripCollaborator collaborator = tripCollaboratorRepository.findByTripAndUser(trip, collaboratorUser)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborator not found"));

        tripCollaboratorRepository.delete(collaborator);
    }

    @Override
    public List<CollaboratorResponse> getTripCollaborators(Long tripId, String userEmail) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        // Check if user has access to trip
        if (!hasAccessToTrip(trip, userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to view collaborators");
        }

        return tripCollaboratorRepository.findByTrip(trip).stream()
                .map(CollaboratorResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void acceptCollaboration(Long tripId, String userEmail) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        TripCollaborator collaborator = tripCollaboratorRepository.findByTripAndUser(trip, user)
                .orElseThrow(() -> new ResourceNotFoundException("Collaboration invitation not found"));

        collaborator.setAccepted(true);
        collaborator.setAcceptedAt(java.time.LocalDateTime.now());

        tripCollaboratorRepository.save(collaborator);
    }

    // Helper methods
    private boolean hasAccessToTrip(Trip trip, String userEmail) {
        // Owner has full access
        if (trip.getUser().getEmail().equals(userEmail)) {
            return true;
        }

        // Check if user is a collaborator
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) return false;

        return tripCollaboratorRepository.existsByTripAndUser(trip, user);
    }

    private boolean hasEditPermission(Trip trip, String userEmail) {
        // Owner has edit permission
        if (trip.getUser().getEmail().equals(userEmail)) {
            return true;
        }

        // Check if user is a collaborator with EDIT or ADMIN permission
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) return false;

        TripCollaborator collaborator = tripCollaboratorRepository.findByTripAndUser(trip, user).orElse(null);
        if (collaborator == null) return false;

        return collaborator.isAccepted() &&
                (collaborator.getPermissionLevel() == TripCollaborator.PermissionLevel.EDIT ||
                        collaborator.getPermissionLevel() == TripCollaborator.PermissionLevel.ADMIN);
    }

    private TripResponse createCompleteTripResponse(Trip trip) {
        TripResponse response = new TripResponse(trip);

        // Add itinerary items
        List<ItineraryItemResponse> items = itineraryItemRepository.findByTrip(trip).stream()
                .map(ItineraryItemResponse::new)
                .collect(Collectors.toList());
        response.setItineraryItems(items);

        // Add collaborators
        List<CollaboratorResponse> collaborators = tripCollaboratorRepository.findByTrip(trip).stream()
                .map(CollaboratorResponse::new)
                .collect(Collectors.toList());
        response.setCollaborators(collaborators);

        return response;
    }
}
