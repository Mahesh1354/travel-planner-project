package com.travelplanner.backend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.dto.external.FlightSearchRequest;
import com.travelplanner.backend.dto.external.FlightResponse;
import com.travelplanner.backend.dto.external.HotelResponse;
import com.travelplanner.backend.dto.external.HotelSearchRequest;
import com.travelplanner.backend.entity.*;
import com.travelplanner.backend.exception.ResourceNotFoundException;
import com.travelplanner.backend.exception.UnauthorizedAccessException;
import com.travelplanner.backend.repository.*;
import com.travelplanner.backend.service.BookingService;
import com.travelplanner.backend.service.external.FlightService;
import com.travelplanner.backend.service.external.HotelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SearchPreferenceRepository searchPreferenceRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItineraryItemRepository itineraryItemRepository;

    @Autowired
    private FlightService flightService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private ObjectMapper objectMapper;

    // ==================== SEARCH OPERATIONS ====================

    @Override
    @Transactional(readOnly = true)
    public List<SearchResult> search(SearchRequest searchRequest, String userEmail) {
        logger.info("Performing search for user: {}, type: {}", userEmail, searchRequest.getSearchType());

        validateSearchRequest(searchRequest);

        List<SearchResult> results = new ArrayList<>();

        try {
            // Apply user preferences if available
            applyUserPreferences(searchRequest, userEmail);

            // Call appropriate external API based on search type
            switch (searchRequest.getSearchType().toUpperCase()) {
                case "FLIGHT":
                    results = searchFlights(searchRequest);
                    break;
                case "HOTEL":
                    results = searchHotels(searchRequest);
                    break;
                case "ACTIVITY":
                    logger.warn("Activity search requested but not implemented yet");
                    throw new UnsupportedOperationException("Activity search not yet implemented");
                default:
                    throw new IllegalArgumentException("Invalid search type: " + searchRequest.getSearchType());
            }

            // Apply sorting and filtering
            results = applySorting(results, searchRequest.getSortBy(), searchRequest.getSortOrder());
            results = applyFilters(results, searchRequest.getFilters());

            logger.info("Search completed, found {} results for user: {}", results.size(), userEmail);
            return results;

        } catch (Exception e) {
            logger.error("Search failed for user {}: {}", userEmail, e.getMessage(), e);
            throw new RuntimeException("Search failed: " + e.getMessage(), e);
        }
    }

    private void validateSearchRequest(SearchRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Search request cannot be null");
        }
        if (request.getSearchType() == null || request.getSearchType().trim().isEmpty()) {
            throw new IllegalArgumentException("Search type is required");
        }
        if (request.getLocation() == null || request.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Location is required");
        }
        if (request.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required");
        }
    }

    private List<SearchResult> searchFlights(SearchRequest searchRequest) {
        logger.debug("Searching flights with request: {}", searchRequest);

        FlightSearchRequest flightRequest = FlightSearchRequest.builder()
                .originCode(searchRequest.getOrigin())
                .destinationCode(searchRequest.getLocation())
                .departureDate(searchRequest.getStartDate())
                .returnDate(searchRequest.getEndDate())
                .adults(searchRequest.getAdults() != null ? searchRequest.getAdults() : 1)
                .cabinClass(searchRequest.getCabinClass())
                .maxResults(searchRequest.getLimit() != null ? searchRequest.getLimit() : 20)
                .build();

        List<FlightResponse> flightResponses = flightService.searchFlights(flightRequest);
        return convertFlightResponsesToSearchResults(flightResponses);
    }

    private List<SearchResult> searchHotels(SearchRequest searchRequest) {
        logger.debug("Searching hotels with request: {}", searchRequest);

        // Validate required fields
        if (searchRequest.getLocation() == null) {
            throw new IllegalArgumentException("Location is required for hotel search");
        }
        if (searchRequest.getStartDate() == null) {
            throw new IllegalArgumentException("Check-in date is required for hotel search");
        }
        if (searchRequest.getEndDate() == null) {
            throw new IllegalArgumentException("Check-out date is required for hotel search");
        }

        HotelSearchRequest hotelRequest = HotelSearchRequest.builder()
                .cityCode(searchRequest.getLocation())
                .hotelName(searchRequest.getHotelName())
                .checkInDate(searchRequest.getStartDate())
                .checkOutDate(searchRequest.getEndDate())
                .adults(searchRequest.getAdults() != null ? searchRequest.getAdults() : 2)
                .rooms(searchRequest.getRooms() != null ? searchRequest.getRooms() : 1)
                .minRating(searchRequest.getMinRating())
                .maxPrice(searchRequest.getMaxPrice() != null ?
                        searchRequest.getMaxPrice().doubleValue() : null)
                .amenities(searchRequest.getAmenities())
                .currency(searchRequest.getCurrency() != null ? searchRequest.getCurrency() : "USD")
                .maxResults(searchRequest.getLimit() != null ? searchRequest.getLimit() : 20)
                .sortBy(searchRequest.getSortBy())
                .build();

        List<HotelResponse> hotelResponses = hotelService.searchHotels(hotelRequest);
        return convertHotelResponsesToSearchResults(hotelResponses);
    }

    // ==================== BOOKING OPERATIONS ====================

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest, String userEmail) {
        logger.info("Creating booking for user: {}, trip: {}", userEmail, bookingRequest.getTripId());

        validateBookingRequest(bookingRequest);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Trip trip = tripRepository.findById(bookingRequest.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + bookingRequest.getTripId()));

        // Check if user has access to trip
        if (!hasAccessToTrip(trip, userEmail)) {
            logger.warn("User {} attempted to book for trip {} without permission", userEmail, bookingRequest.getTripId());
            throw new UnauthorizedAccessException("You don't have permission to book for this trip");
        }

        // Create booking
        Booking booking = buildBookingFromRequest(bookingRequest, user, trip);

        // Link to itinerary item if provided
        if (bookingRequest.getItineraryItemId() != null) {
            linkToItineraryItem(bookingRequest.getItineraryItemId(), trip, booking);
        }

        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Booking created successfully with ID: {} for user: {}", savedBooking.getId(), userEmail);

        return new BookingResponse(savedBooking);
    }

    private void validateBookingRequest(BookingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Booking request cannot be null");
        }
        if (request.getTripId() == null) {
            throw new IllegalArgumentException("Trip ID is required");
        }
        if (request.getBookingType() == null || request.getBookingType().trim().isEmpty()) {
            throw new IllegalArgumentException("Booking type is required");
        }
        if (request.getItemId() == null || request.getItemId().trim().isEmpty()) {
            throw new IllegalArgumentException("Item ID is required");
        }
        if (request.getItemName() == null || request.getItemName().trim().isEmpty()) {
            throw new IllegalArgumentException("Item name is required");
        }
        if (request.getPrice() == null) {
            throw new IllegalArgumentException("Price is required");
        }
    }

    private Booking buildBookingFromRequest(BookingRequest request, User user, Trip trip) {
        Booking booking = new Booking();
        booking.setBookingType(request.getBookingType());
        booking.setExternalId(request.getItemId());
        booking.setProviderName(request.getProviderName() != null ? request.getProviderName() : "MockProvider");
        booking.setItemName(request.getItemName());
        booking.setDescription(request.getDescription());
        booking.setLocation(request.getLocation());
        booking.setPrice(request.getPrice());
        booking.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
        booking.setStartDate(request.getStartDate());
        booking.setEndDate(request.getEndDate());
        booking.setBookingStatus("CONFIRMED");
        booking.setBookingDetails(convertMapToJson(request.getBookingDetails()));
        booking.setCancellationPolicy(request.getCancellationPolicy());
        booking.setTrip(trip);
        booking.setUser(user);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        // Generate confirmation number
        booking.setConfirmationNumber(generateConfirmationNumber());

        return booking;
    }

    private String generateConfirmationNumber() {
        return "MOCK-" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private void linkToItineraryItem(Long itineraryItemId, Trip trip, Booking booking) {
        ItineraryItem itineraryItem = itineraryItemRepository.findById(itineraryItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Itinerary item not found with id: " + itineraryItemId));

        // Verify itinerary item belongs to the same trip
        if (!itineraryItem.getTrip().getId().equals(trip.getId())) {
            throw new IllegalArgumentException("Itinerary item does not belong to the specified trip");
        }

        booking.setItineraryItem(itineraryItem);

        // Update itinerary item booking status
        itineraryItem.setBooked(true);
        itineraryItem.setBookingReference(booking.getConfirmationNumber());
        itineraryItemRepository.save(itineraryItem);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long bookingId, String userEmail) {
        logger.debug("Fetching booking ID: {} for user: {}", bookingId, userEmail);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        // Check if user has access to booking
        if (!booking.getUser().getEmail().equals(userEmail) &&
                !hasAccessToTrip(booking.getTrip(), userEmail)) {
            logger.warn("User {} attempted to access booking {} without permission", userEmail, bookingId);
            throw new UnauthorizedAccessException("You don't have permission to view this booking");
        }

        return new BookingResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getUserBookings(String userEmail, int page, int size,
                                                         String sortBy, String sortDirection) {
        logger.debug("Fetching paginated bookings for user: {} - page: {}, size: {}", userEmail, page, size);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        // Validate and set default sort field
        String validSortBy = (sortBy != null && !sortBy.trim().isEmpty()) ? sortBy : "bookingDate";
        Sort.Direction validDirection = "desc".equalsIgnoreCase(sortDirection) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        // Create pageable with sorting
        Pageable pageable = PageRequest.of(page, size, Sort.by(validDirection, validSortBy));

        // Get paginated results
        Page<Booking> bookingPage = bookingRepository.findByUser(user, pageable);

        // Convert to DTOs
        List<BookingResponse> bookings = bookingPage.getContent()
                .stream()
                .map(BookingResponse::new)
                .collect(Collectors.toList());

        logger.debug("Retrieved {} bookings for user: {} (total: {})",
                bookings.size(), userEmail, bookingPage.getTotalElements());

        // Create page response
        return PageResponse.<BookingResponse>builder()
                .content(bookings)
                .page(bookingPage.getNumber())
                .size(bookingPage.getSize())
                .totalElements(bookingPage.getTotalElements())
                .totalPages(bookingPage.getTotalPages())
                .last(bookingPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getUserBookings(String userEmail, Pageable pageable) {
        logger.debug("Fetching paginated bookings for user: {} with pageable: {}", userEmail, pageable);

        // Extract pagination parameters from Pageable
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();

        // Get sort information
        String sortBy = "bookingDate"; // default
        String sortDirection = "desc"; // default

        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            sortBy = order.getProperty();
            sortDirection = order.getDirection().name().toLowerCase();
        }

        // Call the existing paginated method
        return getUserBookings(userEmail, page, size, sortBy, sortDirection);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getUserBookings(String userEmail) {
        logger.debug("Fetching all bookings for user: {}", userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        return bookingRepository.findByUser(user).stream()
                .map(BookingResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getTripBookings(Long tripId, String userEmail) {
        logger.debug("Fetching bookings for trip ID: {}, user: {}", tripId, userEmail);

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        // Check if user has access to trip
        if (!hasAccessToTrip(trip, userEmail)) {
            logger.warn("User {} attempted to access bookings for trip {} without permission", userEmail, tripId);
            throw new UnauthorizedAccessException("You don't have permission to view bookings for this trip");
        }

        return bookingRepository.findByTrip(trip).stream()
                .map(BookingResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long bookingId, String userEmail) {
        logger.info("Cancelling booking ID: {} for user: {}", bookingId, userEmail);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        // Check if user has access to booking
        if (!booking.getUser().getEmail().equals(userEmail)) {
            logger.warn("User {} attempted to cancel booking {} without permission", userEmail, bookingId);
            throw new UnauthorizedAccessException("Only the booking owner can cancel this booking");
        }

        // Check if booking can be cancelled
        if ("CANCELLED".equals(booking.getBookingStatus())) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        // Update booking status
        booking.setBookingStatus("CANCELLED");
        booking.setUpdatedAt(LocalDateTime.now());

        // Update linked itinerary item if exists
        if (booking.getItineraryItem() != null) {
            ItineraryItem itineraryItem = booking.getItineraryItem();
            itineraryItem.setBooked(false);
            itineraryItem.setBookingReference(null);
            itineraryItemRepository.save(itineraryItem);
        }

        Booking cancelledBooking = bookingRepository.save(booking);
        logger.info("Booking ID: {} cancelled successfully for user: {}", bookingId, userEmail);

        return new BookingResponse(cancelledBooking);
    }

    // ==================== SEARCH PREFERENCES ====================

    @Override
    @Transactional
    public void saveSearchPreference(String userEmail, String preferenceType,
                                     String preferenceKey, String preferenceValue, Integer priority) {
        logger.info("Saving search preference for user: {} - Type: {}, Key: {}",
                userEmail, preferenceType, preferenceKey);

        validatePreferenceInput(preferenceType, preferenceKey);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        // FIXED: Use Optional correctly
        Optional<SearchPreference> existingOpt = searchPreferenceRepository
                .findByUserAndPreferenceTypeAndPreferenceKey(user, preferenceType, preferenceKey);

        if (existingOpt.isPresent()) {
            // Update existing preference
            SearchPreference existing = existingOpt.get();
            existing.setPreferenceValue(preferenceValue);
            existing.setPriority(priority != null ? priority : existing.getPriority());
            existing.setUpdatedAt(LocalDateTime.now());
            searchPreferenceRepository.save(existing);
            logger.debug("Updated existing search preference ID: {}", existing.getId());
        } else {
            // Create new preference
            SearchPreference preference = new SearchPreference();
            preference.setPreferenceType(preferenceType);
            preference.setPreferenceKey(preferenceKey);
            preference.setPreferenceValue(preferenceValue);
            preference.setPriority(priority != null ? priority : 1);
            preference.setUser(user);
            preference.setCreatedAt(LocalDateTime.now());
            preference.setUpdatedAt(LocalDateTime.now());

            searchPreferenceRepository.save(preference);
            logger.debug("Created new search preference");
        }
    }

    private void validatePreferenceInput(String type, String key) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Preference type cannot be empty");
        }
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Preference key cannot be empty");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchPreferenceDto> getUserSearchPreferences(String userEmail, String preferenceType) {
        logger.debug("Fetching search preferences for user: {}", userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        List<SearchPreference> preferences;
        if (preferenceType != null && !preferenceType.trim().isEmpty()) {
            preferences = searchPreferenceRepository.findByUserAndPreferenceTypeOrderByPriorityDesc(user, preferenceType);
        } else {
            preferences = searchPreferenceRepository.findByUserOrderByPriorityDesc(user);
        }

        return preferences.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private SearchPreferenceDto convertToDto(SearchPreference preference) {
        return new SearchPreferenceDto(
                preference.getId(),
                preference.getPreferenceType(),
                preference.getPreferenceKey(),
                preference.getPreferenceValue(),
                preference.getPriority()
        );
    }

    @Override
    @Transactional
    public void deleteSearchPreference(Long preferenceId, String userEmail) {
        logger.info("Deleting search preference ID: {} for user: {}", preferenceId, userEmail);

        SearchPreference preference = searchPreferenceRepository.findById(preferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Search preference not found with id: " + preferenceId));

        // Check if user owns the preference
        if (!preference.getUser().getEmail().equals(userEmail)) {
            logger.warn("User {} attempted to delete preference {} without permission", userEmail, preferenceId);
            throw new UnauthorizedAccessException("You don't have permission to delete this preference");
        }

        searchPreferenceRepository.delete(preference);
        logger.info("Search preference ID: {} deleted successfully", preferenceId);
    }

    // ==================== HELPER METHODS ====================

    private List<SearchResult> convertFlightResponsesToSearchResults(List<FlightResponse> flightResponses) {
        if (flightResponses == null || flightResponses.isEmpty()) {
            return new ArrayList<>();
        }

        return flightResponses.stream()
                .map(this::convertFlightToSearchResult)
                .collect(Collectors.toList());
    }

    private SearchResult convertFlightToSearchResult(FlightResponse flight) {
        SearchResult result = new SearchResult();
        result.setId(UUID.randomUUID().toString());
        result.setSearchType("FLIGHT");
        result.setProvider(flight.getProvider());
        result.setExternalId(flight.getFlightId());
        result.setName(flight.getAirline() + " " + flight.getFlightNumber());
        result.setDescription(String.format("%s → %s", flight.getOriginCode(), flight.getDestinationCode()));
        result.setOrigin(flight.getOriginCode());
        result.setDestination(flight.getDestinationCode());
        result.setDepartureTime(flight.getDepartureTime());
        result.setArrivalTime(flight.getArrivalTime());
        result.setDuration(formatDuration(flight.getDurationMinutes()));
        result.setAirline(flight.getAirline());
        result.setFlightNumber(flight.getFlightNumber());
        result.setPrice(flight.getPrice() != null ? flight.getPrice().doubleValue() : 0.0);
        result.setCurrency(flight.getCurrency());
        result.setAvailableCount(flight.getAvailableSeats());
        result.setCabinClass(flight.getCabinClass());
        result.setCreatedAt(LocalDateTime.now());
        result.setUpdatedAt(LocalDateTime.now());
        return result;
    }

    private List<SearchResult> convertHotelResponsesToSearchResults(List<HotelResponse> hotelResponses) {
        if (hotelResponses == null || hotelResponses.isEmpty()) {
            return new ArrayList<>();
        }

        return hotelResponses.stream()
                .map(this::convertHotelToSearchResult)
                .collect(Collectors.toList());
    }

    private SearchResult convertHotelToSearchResult(HotelResponse hotel) {
        SearchResult result = new SearchResult();
        result.setId(UUID.randomUUID().toString());
        result.setSearchType("HOTEL");
        result.setProvider(hotel.getProvider());
        result.setExternalId(hotel.getHotelId());
        result.setName(hotel.getHotelName());
        result.setDescription(hotel.getDescription());
        result.setLocation(hotel.getCity() + ", " + hotel.getCountry());
        result.setAddress(hotel.getAddress());
        result.setCity(hotel.getCity());
        result.setCountry(hotel.getCountry());
        result.setPrice(hotel.getTotalPrice() != null ? hotel.getTotalPrice().doubleValue() : 0.0);
        result.setCurrency(hotel.getCurrency() != null ? hotel.getCurrency() : "USD");
        result.setRating(hotel.getUserRating());
        result.setStarRating(hotel.getStarRating());
        result.setReviewCount(hotel.getReviewCount());
        result.setHotelChain(hotel.getHotelChain());
        result.setRoomType(hotel.getRoomType());

        if (hotel.getAmenities() != null) {
            result.setAmenities(String.join(", ", hotel.getAmenities()));
        }

        result.setAvailableCount(hotel.getAvailableRooms());
        result.setCheckInTime(hotel.getCheckInTime());
        result.setCheckOutTime(hotel.getCheckOutTime());
        result.setCancellationPolicy(hotel.getCancellationPolicy());

        if (hotel.getImages() != null && !hotel.getImages().isEmpty()) {
            result.setImageUrl(hotel.getImages().get(0));
        }

        result.setAvailableFrom(hotel.getAvailabilityExpires());
        result.setIsAvailable(hotel.getAvailableRooms() != null && hotel.getAvailableRooms() > 0);
        result.setCreatedAt(LocalDateTime.now());
        result.setUpdatedAt(LocalDateTime.now());

        return result;
    }

    private String formatDuration(Integer minutes) {
        if (minutes == null) return "N/A";
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%dh %dm", hours, mins);
    }

    private void applyUserPreferences(SearchRequest searchRequest, String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail).orElse(null);
            if (user == null) return;

            List<SearchPreference> preferences = searchPreferenceRepository
                    .findByUserAndPreferenceTypeOrderByPriorityDesc(user, searchRequest.getSearchType());

            for (SearchPreference pref : preferences) {
                applyPreferenceToSearchRequest(searchRequest, pref);
            }
        } catch (Exception e) {
            logger.error("Error applying user preferences for user {}: {}", userEmail, e.getMessage());
            // Don't fail the search, just log the error
        }
    }

    private void applyPreferenceToSearchRequest(SearchRequest request, SearchPreference pref) {
        switch (pref.getPreferenceKey().toLowerCase()) {
            case "cabin_class":
                if (request.getCabinClass() == null) {
                    request.setCabinClass(pref.getPreferenceValue());
                }
                break;
            case "min_rating":
                if (request.getMinRating() == null) {
                    try {
                        request.setMinRating(Integer.parseInt(pref.getPreferenceValue()));
                    } catch (NumberFormatException e) {
                        logger.debug("Invalid min_rating value: {}", pref.getPreferenceValue());
                    }
                }
                break;
            case "activity_type":
                if (request.getActivityType() == null) {
                    request.setActivityType(pref.getPreferenceValue());
                }
                break;
            case "max_price":
                if (request.getMaxPrice() == null) {
                    try {
                        request.setMaxPrice(Integer.parseInt(pref.getPreferenceValue()));
                    } catch (NumberFormatException e) {
                        logger.debug("Invalid max_price value: {}", pref.getPreferenceValue());
                    }
                }
                break;
            default:
                // Ignore unknown preference keys
                logger.debug("Unknown preference key: {}", pref.getPreferenceKey());
        }
    }

    private List<SearchResult> applySorting(List<SearchResult> results, String sortBy, String sortOrder) {
        if (sortBy == null || results == null || results.isEmpty()) {
            return results;
        }

        boolean ascending = !"desc".equalsIgnoreCase(sortOrder);

        Comparator<SearchResult> comparator = getComparatorForSortField(sortBy);

        if (comparator != null) {
            results.sort(ascending ? comparator : comparator.reversed());
        }

        return results;
    }

    private Comparator<SearchResult> getComparatorForSortField(String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "price":
                return Comparator.comparing(SearchResult::getPrice);
            case "rating":
                return Comparator.comparing(SearchResult::getRating,
                        Comparator.nullsLast(Comparator.reverseOrder()));
            case "name":
                return Comparator.comparing(SearchResult::getName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case "duration":
                return Comparator.comparing(SearchResult::getDuration,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            default:
                return null;
        }
    }

    private List<SearchResult> applyFilters(List<SearchResult> results, Map<String, String> filters) {
        if (filters == null || filters.isEmpty() || results == null || results.isEmpty()) {
            return results;
        }

        return results.stream()
                .filter(result -> matchesAllFilters(result, filters))
                .collect(Collectors.toList());
    }

    private boolean matchesAllFilters(SearchResult result, Map<String, String> filters) {
        return filters.entrySet().stream()
                .allMatch(entry -> matchesFilter(result, entry.getKey(), entry.getValue()));
    }

    private boolean matchesFilter(SearchResult result, String key, String value) {
        if (key == null || value == null) return true;

        try {
            switch (key.toLowerCase()) {
                case "maxprice":
                    return result.getPrice() <= Double.parseDouble(value);
                case "minrating":
                    return result.getRating() >= Double.parseDouble(value);
                case "airline":
                    return result.getAirline() != null &&
                            result.getAirline().equalsIgnoreCase(value);
                case "stops":
                    return result.getStops() != null &&
                            result.getStops() <= Integer.parseInt(value);
                case "hotelchain":
                    return result.getHotelChain() != null &&
                            result.getHotelChain().equalsIgnoreCase(value);
                default:
                    return true;
            }
        } catch (NumberFormatException e) {
            logger.debug("Invalid filter value for key {}: {}", key, value);
            return false;
        }
    }

    private String convertMapToJson(Map<String, Object> map) {
        if (map == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            logger.error("Error converting map to JSON: {}", e.getMessage());
            return "{}";
        }
    }

    private boolean hasAccessToTrip(Trip trip, String userEmail) {
        // Owner has access
        if (trip.getUser().getEmail().equals(userEmail)) {
            return true;
        }

        // Check if user is a collaborator
        return trip.getCollaborators().stream()
                .anyMatch(c -> c.getUser().getEmail().equals(userEmail) && c.isAccepted());
    }
}