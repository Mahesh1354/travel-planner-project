package com.travelplanner.backend.controller;

import com.travelplanner.backend.dto.ApiResponse;
import com.travelplanner.backend.dto.SearchRequest;
import com.travelplanner.backend.dto.SearchResult;
import com.travelplanner.backend.service.BookingService;
import com.travelplanner.backend.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private JwtService jwtService;

    // Helper method to extract user email from JWT token
    private String getCurrentUserEmail(String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Remove "Bearer "
        return jwtService.getEmailFromToken(token);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> search(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody SearchRequest searchRequest) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        List<SearchResult> results = bookingService.search(searchRequest, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Search completed successfully", results));
    }

    // Quick search endpoints for different types
    @GetMapping("/flights")
    public ResponseEntity<ApiResponse> searchFlights(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String date,
            @RequestParam(required = false) String cabinClass) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        List<SearchResult> flights = bookingService.getMockFlights(origin, destination, date, cabinClass);

        return ResponseEntity.ok(ApiResponse.success("Flights retrieved successfully", flights));
    }

    @GetMapping("/hotels")
    public ResponseEntity<ApiResponse> searchHotels(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String location,
            @RequestParam String checkIn,
            @RequestParam String checkOut,
            @RequestParam(required = false) Integer minRating) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        List<SearchResult> hotels = bookingService.getMockHotels(location, checkIn, checkOut, minRating);

        return ResponseEntity.ok(ApiResponse.success("Hotels retrieved successfully", hotels));
    }

    @GetMapping("/activities")
    public ResponseEntity<ApiResponse> searchActivities(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String location,
            @RequestParam String date,
            @RequestParam(required = false) String activityType) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        List<SearchResult> activities = bookingService.getMockActivities(location, date, activityType);

        return ResponseEntity.ok(ApiResponse.success("Activities retrieved successfully", activities));
    }
}
