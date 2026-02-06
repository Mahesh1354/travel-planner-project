package com.travelplanner.backend.service.impl;
import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.entity.*;
import com.travelplanner.backend.exception.ResourceNotFoundException;
import com.travelplanner.backend.exception.UnauthorizedAccessException;
import com.travelplanner.backend.repository.*;
import com.travelplanner.backend.service.BookingService;
import com.travelplanner.backend.service.MockApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

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
    private MockApiService mockApiService;

    @Override
    public List<SearchResult> search(SearchRequest searchRequest, String userEmail) {
        List<SearchResult> results = new ArrayList<>();

        // Apply user preferences if available
        applyUserPreferences(searchRequest, userEmail);

        // Call appropriate mock API based on search type
        switch (searchRequest.getSearchType().toUpperCase()) {
            case "FLIGHT":
                results = mockApiService.searchFlights(
                        searchRequest.getOrigin(),
                        searchRequest.getLocation(),
                        searchRequest.getStartDate().toString(),
                        searchRequest.getCabinClass()
                );
                break;

            case "HOTEL":
                results = mockApiService.searchHotels(
                        searchRequest.getLocation(),
                        searchRequest.getStartDate().toString(),
                        searchRequest.getEndDate() != null ? searchRequest.getEndDate().toString() : null,
                        searchRequest.getMinRating()
                );
                break;

            case "ACTIVITY":
                results = mockApiService.searchActivities(
                        searchRequest.getLocation(),
                        searchRequest.getStartDate().toString(),
                        searchRequest.getActivityType()
                );
                break;

            default:
                throw new IllegalArgumentException("Invalid search type: " + searchRequest.getSearchType());
        }

        // Apply sorting
        results = applySorting(results, searchRequest.getSortBy(), searchRequest.getSortOrder());

        // Apply additional filters
        results = applyFilters(results, searchRequest.getFilters());

        return results;
    }

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Trip trip = tripRepository.findById(bookingRequest.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + bookingRequest.getTripId()));

        // Check if user has access to trip
        if (!hasAccessToTrip(trip, userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to book for this trip");
        }

        // Create booking
        Booking booking = new Booking();
        booking.setBookingType(bookingRequest.getBookingType());
        booking.setExternalId(bookingRequest.getItemId());
        booking.setProviderName(bookingRequest.getProviderName());
        booking.setItemName(bookingRequest.getItemName());
        booking.setDescription(bookingRequest.getDescription());
        booking.setLocation(bookingRequest.getLocation());
        booking.setPrice(bookingRequest.getPrice());
        booking.setCurrency(bookingRequest.getCurrency());
        booking.setStartDate(bookingRequest.getStartDate());
        booking.setEndDate(bookingRequest.getEndDate());
        booking.setBookingStatus("CONFIRMED");

        // Convert booking details to JSON string
        if (bookingRequest.getBookingDetails() != null) {
            booking.setBookingDetails(convertMapToJson(bookingRequest.getBookingDetails()));
        }

        booking.setCancellationPolicy(bookingRequest.getCancellationPolicy());
        booking.setTrip(trip);
        booking.setUser(user);

        // Link to itinerary item if provided
        if (bookingRequest.getItineraryItemId() != null) {
            ItineraryItem itineraryItem = itineraryItemRepository.findById(bookingRequest.getItineraryItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Itinerary item not found"));

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

        Booking savedBooking = bookingRepository.save(booking);
        return new BookingResponse(savedBooking);
    }

    @Override
    public BookingResponse getBooking(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        // Check if user has access to booking
        if (!booking.getUser().getEmail().equals(userEmail) &&
                !hasAccessToTrip(booking.getTrip(), userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to view this booking");
        }

        return new BookingResponse(booking);
    }

    @Override
    public List<BookingResponse> getUserBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return bookingRepository.findByUser(user).stream()
                .map(BookingResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> getTripBookings(Long tripId, String userEmail) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

        // Check if user has access to trip
        if (!hasAccessToTrip(trip, userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to view bookings for this trip");
        }

        return bookingRepository.findByTrip(trip).stream()
                .map(BookingResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        // Check if user has access to booking
        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedAccessException("Only the booking owner can cancel this booking");
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
        return new BookingResponse(cancelledBooking);
    }

    @Override
    @Transactional
    public void saveSearchPreference(String userEmail, String preferenceType,
                                     String preferenceKey, String preferenceValue, Integer priority) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if preference already exists
        SearchPreference existing = searchPreferenceRepository
                .findByUserAndPreferenceTypeAndPreferenceKey(user, preferenceType, preferenceKey);

        if (existing != null) {
            // Update existing preference
            existing.setPreferenceValue(preferenceValue);
            existing.setPriority(priority != null ? priority : existing.getPriority());
            searchPreferenceRepository.save(existing);
        } else {
            // Create new preference
            SearchPreference preference = new SearchPreference();
            preference.setPreferenceType(preferenceType);
            preference.setPreferenceKey(preferenceKey);
            preference.setPreferenceValue(preferenceValue);
            preference.setPriority(priority != null ? priority : 1);
            preference.setUser(user);

            searchPreferenceRepository.save(preference);
        }
    }

    @Override
    public List<SearchPreferenceDto> getUserSearchPreferences(String userEmail, String preferenceType) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<SearchPreference> preferences;
        if (preferenceType != null) {
            preferences = searchPreferenceRepository.findByUserAndPreferenceType(user, preferenceType);
        } else {
            preferences = searchPreferenceRepository.findByUser(user);
        }

        return preferences.stream()
                .map(p -> new SearchPreferenceDto(
                        p.getId(),
                        p.getPreferenceType(),
                        p.getPreferenceKey(),
                        p.getPreferenceValue(),
                        p.getPriority()
                ))
                .collect(Collectors.toList());
    }
    @Override
    @Transactional
    public void deleteSearchPreference(Long preferenceId, String userEmail) {
        SearchPreference preference = searchPreferenceRepository.findById(preferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Search preference not found"));

        // Check if user owns the preference
        if (!preference.getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedAccessException("You don't have permission to delete this preference");
        }

        searchPreferenceRepository.delete(preference);
    }

    @Override
    public List<SearchResult> getMockFlights(String origin, String destination,
                                             String startDate, String cabinClass) {
        return mockApiService.searchFlights(origin, destination, startDate, cabinClass);
    }

    @Override
    public List<SearchResult> getMockHotels(String location, String startDate,
                                            String endDate, Integer minRating) {
        return mockApiService.searchHotels(location, startDate, endDate, minRating);
    }

    @Override
    public List<SearchResult> getMockActivities(String location, String startDate,
                                                String activityType) {
        return mockApiService.searchActivities(location, startDate, activityType);
    }

    // Helper methods
    private void applyUserPreferences(SearchRequest searchRequest, String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail).orElse(null);
            if (user == null) return;

            List<SearchPreference> preferences = searchPreferenceRepository
                    .findByUserAndPreferenceType(user, searchRequest.getSearchType());

            for (SearchPreference pref : preferences) {
                switch (pref.getPreferenceKey().toLowerCase()) {
                    case "cabin_class":
                        if (searchRequest.getCabinClass() == null) {
                            searchRequest.setCabinClass(pref.getPreferenceValue());
                        }
                        break;
                    case "min_rating":
                        if (searchRequest.getMinRating() == null) {
                            try {
                                searchRequest.setMinRating(Integer.parseInt(pref.getPreferenceValue()));
                            } catch (NumberFormatException e) {
                                // Ignore invalid rating
                            }
                        }
                        break;
                    case "activity_type":
                        if (searchRequest.getActivityType() == null) {
                            searchRequest.setActivityType(pref.getPreferenceValue());
                        }
                        break;
                    case "max_price":
                        if (searchRequest.getMaxPrice() == null) {
                            try {
                                searchRequest.setMaxPrice(Integer.parseInt(pref.getPreferenceValue()));
                            } catch (NumberFormatException e) {
                                // Ignore invalid price
                            }
                        }
                        break;
                }
            }
        } catch (Exception e) {
            // Log error but don't fail the search
            System.err.println("Error applying user preferences: " + e.getMessage());
        }
    }

    private List<SearchResult> applySorting(List<SearchResult> results, String sortBy, String sortOrder) {
        if (sortBy == null || results.isEmpty()) {
            return results;
        }

        results.sort((a, b) -> {
            int comparison = 0;

            switch (sortBy.toLowerCase()) {
                case "price":
                    comparison = Double.compare(a.getPrice(), b.getPrice());
                    break;
                case "rating":
                    comparison = Double.compare(b.getRating(), a.getRating()); // Descending by default
                    break;
                case "duration":
                    // For flights only
                    if (a.getSearchType().equals("FLIGHT") && b.getSearchType().equals("FLIGHT")) {
                        comparison = compareDurations(a.getDuration(), b.getDuration());
                    }
                    break;
                default:
                    comparison = 0;
            }

            return "desc".equalsIgnoreCase(sortOrder) ? -comparison : comparison;
        });

        return results;
    }

    private List<SearchResult> applyFilters(List<SearchResult> results, Map<String, String> filters) {
        if (filters == null || filters.isEmpty() || results.isEmpty()) {
            return results;
        }

        return results.stream()
                .filter(result -> {
                    for (Map.Entry<String, String> filter : filters.entrySet()) {
                        if (!matchesFilter(result, filter.getKey(), filter.getValue())) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    private boolean matchesFilter(SearchResult result, String key, String value) {
        if (key == null || value == null) return true;

        switch (key.toLowerCase()) {
            case "maxprice":
                return result.getPrice() <= Double.parseDouble(value);
            case "minrating":
                return result.getRating() >= Double.parseDouble(value);
            case "airline":
                return result.getAirline() != null && result.getAirline().equalsIgnoreCase(value);
            case "stops":
                return result.getStops() != null && result.getStops() <= Integer.parseInt(value);
            case "hotelchain":
                return result.getHotelChain() != null && result.getHotelChain().equalsIgnoreCase(value);
            default:
                return true;
        }
    }

    private int compareDurations(String duration1, String duration2) {
        if (duration1 == null || duration2 == null) return 0;

        try {
            // Parse "Xh Ym" format
            String[] parts1 = duration1.split(" ");
            String[] parts2 = duration2.split(" ");

            int hours1 = Integer.parseInt(parts1[0].replace("h", ""));
            int hours2 = Integer.parseInt(parts2[0].replace("h", ""));

            return Integer.compare(hours1, hours2);
        } catch (Exception e) {
            return 0;
        }
    }

    private String convertMapToJson(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }

        try {
            StringBuilder json = new StringBuilder("{");
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                json.append("\"").append(entry.getKey()).append("\":\"")
                        .append(entry.getValue()).append("\",");
            }
            json.deleteCharAt(json.length() - 1); // Remove trailing comma
            json.append("}");
            return json.toString();
        } catch (Exception e) {
            return "{}";
        }
    }

    private boolean hasAccessToTrip(Trip trip, String userEmail) {
        // Owner has access
        if (trip.getUser().getEmail().equals(userEmail)) {
            return true;
        }

        // Check if user is a collaborator
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) return false;

        return trip.getCollaborators().stream()
                .anyMatch(c -> c.getUser().getEmail().equals(userEmail) && c.isAccepted());
    }
}
