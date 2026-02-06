package com.travelplanner.backend.service;


import com.travelplanner.backend.dto.*;

import java.util.List;

public interface BookingService {

    // Search operations
    List<SearchResult> search(SearchRequest searchRequest, String userEmail);

    // Booking operations
    BookingResponse createBooking(BookingRequest bookingRequest, String userEmail);
    BookingResponse getBooking(Long bookingId, String userEmail);
    List<BookingResponse> getUserBookings(String userEmail);
    List<BookingResponse> getTripBookings(Long tripId, String userEmail);
    BookingResponse cancelBooking(Long bookingId, String userEmail);

    // Search preferences
    void saveSearchPreference(String userEmail, String preferenceType,
                              String preferenceKey, String preferenceValue, Integer priority);
    List<SearchPreferenceDto> getUserSearchPreferences(String userEmail, String preferenceType);
    void deleteSearchPreference(Long preferenceId, String userEmail);

    // Mock data generation
    List<SearchResult> getMockFlights(String origin, String destination,
                                      String startDate, String cabinClass);
    List<SearchResult> getMockHotels(String location, String startDate,
                                     String endDate, Integer minRating);
    List<SearchResult> getMockActivities(String location, String startDate,
                                         String activityType);
}
