package com.travelplanner.backend.service;

import com.travelplanner.backend.dto.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {

    // Search operations
    List<SearchResult> search(SearchRequest searchRequest, String userEmail);

    // Booking operations
    BookingResponse createBooking(BookingRequest bookingRequest, String userEmail);
    BookingResponse getBooking(Long bookingId, String userEmail);

    // Paginated methods
    PageResponse<BookingResponse> getUserBookings(String userEmail, int page, int size, String sortBy, String sortDirection);
    PageResponse<BookingResponse> getUserBookings(String userEmail, Pageable pageable);

    // Non-paginated method
    List<BookingResponse> getUserBookings(String userEmail);

    List<BookingResponse> getTripBookings(Long tripId, String userEmail);
    BookingResponse cancelBooking(Long bookingId, String userEmail);

    // Search preferences
    void saveSearchPreference(String userEmail, String preferenceType,
                              String preferenceKey, String preferenceValue, Integer priority);
    List<SearchPreferenceDto> getUserSearchPreferences(String userEmail, String preferenceType);
    void deleteSearchPreference(Long preferenceId, String userEmail);
}