package com.travelplanner.backend.controller;
import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.service.BookingService;
import com.travelplanner.backend.service.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private JwtService jwtService;

    // Helper method to extract user email from JWT token
    private String getCurrentUserEmail(String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Remove "Bearer "
        return jwtService.getEmailFromToken(token);
    }

    // Booking operations
    @PostMapping
    public ResponseEntity<ApiResponse> createBooking(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody BookingRequest bookingRequest) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        BookingResponse response = bookingService.createBooking(bookingRequest, userEmail);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getUserBookings(
            @RequestHeader("Authorization") String authorizationHeader) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        List<BookingResponse> bookings = bookingService.getUserBookings(userEmail);

        return ResponseEntity.ok(ApiResponse.success("Bookings retrieved successfully", bookings));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse> getBooking(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long bookingId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        BookingResponse booking = bookingService.getBooking(bookingId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Booking retrieved successfully", booking));
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<ApiResponse> getTripBookings(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long tripId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        List<BookingResponse> bookings = bookingService.getTripBookings(tripId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Trip bookings retrieved successfully", bookings));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse> cancelBooking(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long bookingId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        BookingResponse booking = bookingService.cancelBooking(bookingId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", booking));
    }

    // Search preference operations
    @PostMapping("/preferences")
    public ResponseEntity<ApiResponse> saveSearchPreference(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody SearchPreferenceRequest request) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        bookingService.saveSearchPreference(
                userEmail,
                request.getPreferenceType(),
                request.getPreferenceKey(),
                request.getPreferenceValue(),
                request.getPriority()
        );

        return ResponseEntity.ok(ApiResponse.success("Search preference saved successfully", null));
    }

    @GetMapping("/preferences")
    public ResponseEntity<ApiResponse> getUserSearchPreferences(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(required = false) String preferenceType) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        List<SearchPreferenceDto> preferences =
                bookingService.getUserSearchPreferences(userEmail, preferenceType);

        return ResponseEntity.ok(ApiResponse.success("Search preferences retrieved successfully", preferences));
    }

    @DeleteMapping("/preferences/{preferenceId}")
    public ResponseEntity<ApiResponse> deleteSearchPreference(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long preferenceId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        bookingService.deleteSearchPreference(preferenceId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Search preference deleted successfully", null));
    }

    // Inner DTO class for search preference request
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchPreferenceRequest {
        private String preferenceType;
        private String preferenceKey;
        private String preferenceValue;
        private Integer priority;
    }
}
