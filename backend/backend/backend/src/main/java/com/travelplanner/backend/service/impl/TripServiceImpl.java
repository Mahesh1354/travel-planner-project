package com.travelplanner.backend.service.impl;

import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.entity.*;
import com.travelplanner.backend.exception.ResourceNotFoundException;
import com.travelplanner.backend.exception.UnauthorizedAccessException;
import com.travelplanner.backend.repository.*;
import com.travelplanner.backend.service.TripService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripServiceImpl implements TripService {

    private static final Logger logger = LoggerFactory.getLogger(TripServiceImpl.class);

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ItineraryItemRepository itineraryItemRepository;

    @Autowired
    private TripCollaboratorRepository tripCollaboratorRepository;

    @Autowired
    private UserRepository userRepository;

    // ==================== TRIP CRUD OPERATIONS ====================

    @Override
    @Transactional
    public TripResponse createTrip(String userEmail, TripRequest tripRequest) {
        logger.info("Creating new trip for user: {}", userEmail);

        validateTripRequest(tripRequest);

        User user = getUserByEmail(userEmail);

        Trip trip = buildTripFromRequest(tripRequest, user);
        Trip savedTrip = tripRepository.save(trip);

        logger.info("Trip created successfully with ID: {} for user: {}", savedTrip.getId(), userEmail);

        return new TripResponse(savedTrip);
    }

    @Override
    @Transactional(readOnly = true)
    public TripResponse getTripById(Long tripId, String userEmail) {
        logger.debug("Fetching trip ID: {} for user: {}", tripId, userEmail);

        Trip trip = findTripById(tripId);

        // Check if user has access
        if (!hasAccessToTrip(trip, userEmail)) {
            logger.warn("User {} attempted to access trip {} without permission", userEmail, tripId);
            throw new UnauthorizedAccessException("You don't have permission to view this trip");
        }

        return createCompleteTripResponse(trip);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripResponse> getUserTrips(String userEmail) {
        logger.debug("Fetching all trips for user: {}", userEmail);

        User user = getUserByEmail(userEmail);

        // Get trips owned by user
        List<Trip> ownedTrips = tripRepository.findByUser(user);

        // Get trips where user is collaborator
        List<TripCollaborator> collaborations = tripCollaboratorRepository.findByUser(user);
        List<Trip> collaboratedTrips = collaborations.stream()
                .filter(TripCollaborator::isAccepted) // Only accepted collaborations
                .map(TripCollaborator::getTrip)
                .collect(Collectors.toList());

        // Combine and remove duplicates
        ownedTrips.addAll(collaboratedTrips);

        // Remove duplicates by ID
        List<Trip> uniqueTrips = ownedTrips.stream()
                .distinct()
                .collect(Collectors.toList());

        logger.debug("Found {} trips for user: {}", uniqueTrips.size(), userEmail);

        return uniqueTrips.stream()
                .map(this::createBasicTripResponse) // Use basic response for list views
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TripResponse updateTrip(Long tripId, String userEmail, TripRequest tripRequest) {
        logger.info("Updating trip ID: {} for user: {}", tripId, userEmail);

        validateTripRequest(tripRequest);

        Trip trip = findTripById(tripId);

        // Check if user has edit permission
        if (!hasEditPermission(trip, userEmail)) {
            logger.warn("User {} attempted to update trip {} without permission", userEmail, tripId);
            throw new UnauthorizedAccessException("You don't have permission to edit this trip");
        }

        updateTripFromRequest(trip, tripRequest);
        trip.setUpdatedAt(LocalDateTime.now());

        Trip updatedTrip = tripRepository.save(trip);
        logger.info("Trip ID: {} updated successfully by user: {}", tripId, userEmail);

        return createCompleteTripResponse(updatedTrip);
    }

    @Override
    @Transactional
    public void deleteTrip(Long tripId, String userEmail) {
        logger.info("Deleting trip ID: {} for user: {}", tripId, userEmail);

        Trip trip = findTripById(tripId);

        // Check if user is the owner
        if (!trip.getUser().getEmail().equals(userEmail)) {
            logger.warn("User {} attempted to delete trip {} without owner permission", userEmail, tripId);
            throw new UnauthorizedAccessException("Only trip owner can delete the trip");
        }

        // Delete associated collaborators first (to avoid FK constraints)
        tripCollaboratorRepository.deleteByTrip(trip);

        // Delete associated itinerary items
        itineraryItemRepository.deleteByTrip(trip);

        // Delete the trip
        tripRepository.delete(trip);

        logger.info("Trip ID: {} deleted successfully by user: {}", tripId, userEmail);
    }

    @Override
    @Transactional
    public TripResponse duplicateTrip(Long tripId, String userEmail) {
        logger.info("Duplicating trip ID: {} for user: {}", tripId, userEmail);

        Trip originalTrip = findTripById(tripId);

        // Check if user has access to original trip
        if (!hasAccessToTrip(originalTrip, userEmail)) {
            logger.warn("User {} attempted to duplicate trip {} without permission", userEmail, tripId);
            throw new UnauthorizedAccessException("You don't have permission to duplicate this trip");
        }

        User user = getUserByEmail(userEmail);

        // Create new trip with "Copy of" prefix
        Trip duplicateTrip = new Trip();
        duplicateTrip.setTripName("Copy of " + originalTrip.getTripName());
        duplicateTrip.setDescription(originalTrip.getDescription());
        duplicateTrip.setStartDate(originalTrip.getStartDate());  // LocalDate from original
        duplicateTrip.setEndDate(originalTrip.getEndDate());      // LocalDate from original
        duplicateTrip.setDestination(originalTrip.getDestination());
        duplicateTrip.setBudget(originalTrip.getBudget());
        duplicateTrip.setNotes(originalTrip.getNotes());
        duplicateTrip.setPublicTrip(false); // Duplicates are private by default
        duplicateTrip.setUser(user);
        duplicateTrip.setCreatedAt(LocalDateTime.now());          // LocalDateTime - OK
        duplicateTrip.setUpdatedAt(LocalDateTime.now());          // LocalDateTime - OK

        Trip savedTrip = tripRepository.save(duplicateTrip);

        // Duplicate itinerary items
        duplicateItineraryItems(originalTrip, savedTrip);

        logger.info("Trip duplicated successfully - Original ID: {}, New ID: {}, User: {}",
                tripId, savedTrip.getId(), userEmail);

        return createCompleteTripResponse(savedTrip);
    }

    // ==================== ITINERARY ITEM OPERATIONS ====================

    @Override
    @Transactional
    public ItineraryItemResponse addItineraryItem(Long tripId, String userEmail, ItineraryItemRequest itemRequest) {
        logger.info("Adding itinerary item to trip ID: {} for user: {}", tripId, userEmail);

        validateItineraryItemRequest(itemRequest);

        Trip trip = findTripById(tripId);

        // Check if user has edit permission
        if (!hasEditPermission(trip, userEmail)) {
            logger.warn("User {} attempted to add item to trip {} without permission", userEmail, tripId);
            throw new UnauthorizedAccessException("You don't have permission to add items to this trip");
        }

        ItineraryItem item = buildItineraryItemFromRequest(itemRequest, trip);
        ItineraryItem savedItem = itineraryItemRepository.save(item);

        logger.info("Itinerary item added successfully with ID: {} to trip ID: {}", savedItem.getId(), tripId);

        return new ItineraryItemResponse(savedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItineraryItemResponse> getTripItineraryItems(Long tripId, String userEmail) {
        logger.debug("Fetching itinerary items for trip ID: {}, user: {}", tripId, userEmail);

        Trip trip = findTripById(tripId);

        // Check if user has access to trip
        if (!hasAccessToTrip(trip, userEmail)) {
            logger.warn("User {} attempted to view items for trip {} without permission", userEmail, tripId);
            throw new UnauthorizedAccessException("You don't have permission to view this trip's items");
        }

        List<ItineraryItem> items = itineraryItemRepository.findByTripOrderByItemDateAsc(trip);

        logger.debug("Found {} items for trip ID: {}", items.size(), tripId);

        return items.stream()
                .map(ItineraryItemResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItineraryItemResponse updateItineraryItem(Long itemId, String userEmail, ItineraryItemRequest itemRequest) {
        logger.info("Updating itinerary item ID: {} for user: {}", itemId, userEmail);

        validateItineraryItemRequest(itemRequest);

        ItineraryItem item = findItineraryItemById(itemId);

        // Check if user has edit permission for the trip
        if (!hasEditPermission(item.getTrip(), userEmail)) {
            logger.warn("User {} attempted to update item {} without permission", userEmail, itemId);
            throw new UnauthorizedAccessException("You don't have permission to update this item");
        }

        updateItineraryItemFromRequest(item, itemRequest);
        item.setUpdatedAt(LocalDateTime.now());

        ItineraryItem updatedItem = itineraryItemRepository.save(item);
        logger.info("Itinerary item ID: {} updated successfully by user: {}", itemId, userEmail);

        return new ItineraryItemResponse(updatedItem);
    }

    @Override
    @Transactional
    public void deleteItineraryItem(Long itemId, String userEmail) {
        logger.info("Deleting itinerary item ID: {} for user: {}", itemId, userEmail);

        ItineraryItem item = findItineraryItemById(itemId);

        // Check if user has edit permission for the trip
        if (!hasEditPermission(item.getTrip(), userEmail)) {
            logger.warn("User {} attempted to delete item {} without permission", userEmail, itemId);
            throw new UnauthorizedAccessException("You don't have permission to delete this item");
        }

        itineraryItemRepository.delete(item);
        logger.info("Itinerary item ID: {} deleted successfully by user: {}", itemId, userEmail);
    }

    // ==================== COLLABORATOR OPERATIONS ====================

    @Override
    @Transactional
    public CollaboratorResponse addCollaborator(Long tripId, String ownerEmail, CollaboratorRequest collaboratorRequest) {
        logger.info("Adding collaborator to trip ID: {} by owner: {}", tripId, ownerEmail);

        Trip trip = findTripById(tripId);

        // Check if requester is trip owner
        if (!trip.getUser().getEmail().equals(ownerEmail)) {
            logger.warn("User {} attempted to add collaborator to trip {} without owner permission", ownerEmail, tripId);
            throw new UnauthorizedAccessException("Only trip owner can add collaborators");
        }

        // Find collaborator user by email
        User collaboratorUser = userRepository.findByEmail(collaboratorRequest.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + collaboratorRequest.getUserEmail()));

        // Check if not adding yourself
        if (collaboratorUser.getEmail().equals(ownerEmail)) {
            logger.warn("Owner {} attempted to add themselves as collaborator", ownerEmail);
            throw new IllegalArgumentException("Cannot add yourself as collaborator");
        }

        // Check if already a collaborator
        if (tripCollaboratorRepository.existsByTripAndUser(trip, collaboratorUser)) {
            logger.warn("User {} is already a collaborator on trip {}", collaboratorUser.getEmail(), tripId);
            throw new IllegalArgumentException("User is already a collaborator on this trip");
        }

        // Validate permission level
        TripCollaborator.PermissionLevel permissionLevel;
        try {
            permissionLevel = TripCollaborator.PermissionLevel.valueOf(collaboratorRequest.getPermissionLevel().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid permission level. Allowed values: VIEW, EDIT, ADMIN");
        }

        TripCollaborator collaborator = new TripCollaborator();
        collaborator.setTrip(trip);
        collaborator.setUser(collaboratorUser);
        collaborator.setPermissionLevel(permissionLevel);
        collaborator.setAccepted(false);
        collaborator.setInvitedAt(LocalDateTime.now());

        TripCollaborator savedCollaborator = tripCollaboratorRepository.save(collaborator);
        logger.info("Collaborator {} added to trip {} by owner {}",
                collaboratorUser.getEmail(), tripId, ownerEmail);

        return new CollaboratorResponse(savedCollaborator);
    }

    @Override
    @Transactional
    public void removeCollaborator(Long tripId, String ownerEmail, String collaboratorEmail) {
        logger.info("Removing collaborator {} from trip ID: {} by owner: {}", collaboratorEmail, tripId, ownerEmail);

        Trip trip = findTripById(tripId);

        // Check if requester is trip owner
        if (!trip.getUser().getEmail().equals(ownerEmail)) {
            logger.warn("User {} attempted to remove collaborator from trip {} without owner permission", ownerEmail, tripId);
            throw new UnauthorizedAccessException("Only trip owner can remove collaborators");
        }

        User collaboratorUser = userRepository.findByEmail(collaboratorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + collaboratorEmail));

        TripCollaborator collaborator = tripCollaboratorRepository.findByTripAndUser(trip, collaboratorUser)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborator not found"));

        tripCollaboratorRepository.delete(collaborator);
        logger.info("Collaborator {} removed from trip {} by owner {}", collaboratorEmail, tripId, ownerEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollaboratorResponse> getTripCollaborators(Long tripId, String userEmail) {
        logger.debug("Fetching collaborators for trip ID: {}, user: {}", tripId, userEmail);

        Trip trip = findTripById(tripId);

        // Check if user has access to trip
        if (!hasAccessToTrip(trip, userEmail)) {
            logger.warn("User {} attempted to view collaborators for trip {} without permission", userEmail, tripId);
            throw new UnauthorizedAccessException("You don't have permission to view collaborators");
        }

        List<TripCollaborator> collaborators = tripCollaboratorRepository.findByTrip(trip);

        logger.debug("Found {} collaborators for trip ID: {}", collaborators.size(), tripId);

        return collaborators.stream()
                .map(CollaboratorResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void acceptCollaboration(Long tripId, String userEmail) {
        logger.info("User {} accepting collaboration for trip ID: {}", userEmail, tripId);

        Trip trip = findTripById(tripId);
        User user = getUserByEmail(userEmail);

        TripCollaborator collaborator = tripCollaboratorRepository.findByTripAndUser(trip, user)
                .orElseThrow(() -> new ResourceNotFoundException("Collaboration invitation not found for trip ID: " + tripId));

        if (collaborator.isAccepted()) {
            logger.warn("User {} already accepted collaboration for trip {}", userEmail, tripId);
            throw new IllegalStateException("Collaboration already accepted");
        }

        collaborator.setAccepted(true);
        collaborator.setAcceptedAt(LocalDateTime.now());

        tripCollaboratorRepository.save(collaborator);
        logger.info("User {} accepted collaboration for trip ID: {}", userEmail, tripId);
    }

    // ==================== HELPER METHODS ====================

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private Trip findTripById(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));
    }

    private ItineraryItem findItineraryItemById(Long itemId) {
        return itineraryItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Itinerary item not found with id: " + itemId));
    }

    private void validateTripRequest(TripRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Trip request cannot be null");
        }
        if (request.getTripName() == null || request.getTripName().trim().isEmpty()) {
            throw new IllegalArgumentException("Trip name is required");
        }
        if (request.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        if (request.getEndDate() == null) {
            throw new IllegalArgumentException("End date is required");
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }
    }

    private void validateItineraryItemRequest(ItineraryItemRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Itinerary item request cannot be null");
        }
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Item title is required");
        }
        if (request.getItemDate() == null) {
            throw new IllegalArgumentException("Item date is required");
        }
    }

    private Trip buildTripFromRequest(TripRequest request, User user) {
        Trip trip = new Trip();
        trip.setTripName(request.getTripName().trim());
        trip.setDescription(request.getDescription());
        trip.setStartDate(request.getStartDate());  // LocalDate from request - OK
        trip.setEndDate(request.getEndDate());      // LocalDate from request - OK
        trip.setDestination(request.getDestination());
        trip.setBudget(request.getBudget());
        trip.setNotes(request.getNotes());
        trip.setPublicTrip(request.isPublic());
        trip.setUser(user);
        trip.setCreatedAt(LocalDateTime.now());     // LocalDateTime - OK
        trip.setUpdatedAt(LocalDateTime.now());     // LocalDateTime - OK
        return trip;
    }

    private void updateTripFromRequest(Trip trip, TripRequest request) {
        trip.setTripName(request.getTripName().trim());
        trip.setDescription(request.getDescription());
        trip.setStartDate(request.getStartDate());  // LocalDate from request - OK
        trip.setEndDate(request.getEndDate());      // LocalDate from request - OK
        trip.setDestination(request.getDestination());
        trip.setBudget(request.getBudget());
        trip.setNotes(request.getNotes());
        trip.setPublicTrip(request.isPublic());
        // updatedAt is set in the calling method
    }

    private ItineraryItem buildItineraryItemFromRequest(ItineraryItemRequest request, Trip trip) {
        ItineraryItem item = new ItineraryItem();
        item.setTitle(request.getTitle().trim());
        item.setDescription(request.getDescription());
        item.setItemDate(request.getItemDate());
        item.setLocation(request.getLocation());
        item.setItemType(request.getItemType());
        item.setCost(request.getCost());
        item.setNotes(request.getNotes());
        item.setBooked(request.isBooked());
        item.setBookingReference(request.getBookingReference());
        item.setTrip(trip);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        return item;
    }

    private void updateItineraryItemFromRequest(ItineraryItem item, ItineraryItemRequest request) {
        item.setTitle(request.getTitle().trim());
        item.setDescription(request.getDescription());
        item.setItemDate(request.getItemDate());
        item.setLocation(request.getLocation());
        item.setItemType(request.getItemType());
        item.setCost(request.getCost());
        item.setNotes(request.getNotes());
        item.setBooked(request.isBooked());
        item.setBookingReference(request.getBookingReference());
        // updatedAt is set in the calling method
    }

    private void duplicateItineraryItems(Trip sourceTrip, Trip destinationTrip) {
        for (ItineraryItem originalItem : sourceTrip.getItineraryItems()) {
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
            duplicateItem.setTrip(destinationTrip);
            duplicateItem.setCreatedAt(LocalDateTime.now());
            duplicateItem.setUpdatedAt(LocalDateTime.now());

            itineraryItemRepository.save(duplicateItem);
        }
    }

    private boolean hasAccessToTrip(Trip trip, String userEmail) {
        // Owner has full access
        if (trip.getUser().getEmail().equals(userEmail)) {
            return true;
        }

        // Check if user is an accepted collaborator
        return tripCollaboratorRepository.existsByTripAndUserEmailAndIsAcceptedTrue(trip, userEmail);
    }

    private boolean hasEditPermission(Trip trip, String userEmail) {
        // Owner has edit permission
        if (trip.getUser().getEmail().equals(userEmail)) {
            return true;
        }

        // Check if user is a collaborator with EDIT or ADMIN permission
        return tripCollaboratorRepository.existsByTripAndUserEmailAndPermissionLevelInAndIsAcceptedTrue(
                trip,
                userEmail,
                List.of(TripCollaborator.PermissionLevel.EDIT, TripCollaborator.PermissionLevel.ADMIN)
        );
    }

    private TripResponse createBasicTripResponse(Trip trip) {
        return new TripResponse(trip);
    }

    private TripResponse createCompleteTripResponse(Trip trip) {
        TripResponse response = new TripResponse(trip);

        // Add itinerary items
        List<ItineraryItemResponse> items = itineraryItemRepository.findByTripOrderByItemDateAsc(trip).stream()
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