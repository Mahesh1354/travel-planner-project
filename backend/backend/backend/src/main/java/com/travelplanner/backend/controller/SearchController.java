package com.travelplanner.backend.controller;

import com.travelplanner.backend.dto.ApiResponse;
import com.travelplanner.backend.dto.SearchRequest;
import com.travelplanner.backend.dto.SearchResult;
import com.travelplanner.backend.service.BookingService;
import com.travelplanner.backend.service.JwtService;
import com.travelplanner.backend.service.external.FlightService;
import com.travelplanner.backend.service.external.HotelService;
import com.travelplanner.backend.dto.external.FlightSearchRequest;
import com.travelplanner.backend.dto.external.FlightResponse;
import com.travelplanner.backend.dto.external.HotelSearchRequest;
import com.travelplanner.backend.dto.external.HotelResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private FlightService flightService;

    @Autowired
    private HotelService hotelService;

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

    // ✅ UPDATED: Real flight search using Amadeus API
    @GetMapping("/flights")
    public ResponseEntity<ApiResponse> searchFlights(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String date,
            @RequestParam(required = false) String returnDate,
            @RequestParam(required = false, defaultValue = "1") Integer adults,
            @RequestParam(required = false) String cabinClass,
            @RequestParam(required = false, defaultValue = "USD") String currency) {

        String userEmail = getCurrentUserEmail(authorizationHeader);

        FlightSearchRequest flightRequest = FlightSearchRequest.builder()
                .originCode(origin)
                .destinationCode(destination)
                .departureDate(LocalDate.parse(date))
                .returnDate(returnDate != null ? LocalDate.parse(returnDate) : null)
                .adults(adults)
                .cabinClass(cabinClass != null ? cabinClass : "ECONOMY")
                .currency(currency)
                .maxResults(20)
                .build();

        List<FlightResponse> flightResponses = flightService.searchFlights(flightRequest);

        // Convert FlightResponse to SearchResult
        List<SearchResult> results = convertFlightResponsesToSearchResults(flightResponses);

        return ResponseEntity.ok(ApiResponse.success("Flights retrieved successfully", results));
    }

    // ✅ UPDATED: Real hotel search using Amadeus API
    @GetMapping("/hotels")
    public ResponseEntity<ApiResponse> searchHotels(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String location,
            @RequestParam String checkIn,
            @RequestParam String checkOut,
            @RequestParam(required = false, defaultValue = "2") Integer adults,
            @RequestParam(required = false, defaultValue = "1") Integer rooms,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String amenities,
            @RequestParam(required = false, defaultValue = "USD") String currency) {

        String userEmail = getCurrentUserEmail(authorizationHeader);

        HotelSearchRequest hotelRequest = HotelSearchRequest.builder()
                .cityCode(location)
                .checkInDate(LocalDate.parse(checkIn))
                .checkOutDate(LocalDate.parse(checkOut))
                .adults(adults)
                .rooms(rooms)
                .minRating(minRating)
                .maxPrice(maxPrice != null ? maxPrice.doubleValue() : null)
                .amenities(amenities)
                .currency(currency)
                .maxResults(20)
                .build();

        List<HotelResponse> hotelResponses = hotelService.searchHotels(hotelRequest);

        // Convert HotelResponse to SearchResult
        List<SearchResult> results = convertHotelResponsesToSearchResults(hotelResponses);

        return ResponseEntity.ok(ApiResponse.success("Hotels retrieved successfully", results));
    }

    // ✅ UPDATED: Activities search (to be implemented)
    @GetMapping("/activities")
    public ResponseEntity<ApiResponse> searchActivities(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String location,
            @RequestParam String date,
            @RequestParam(required = false) String activityType) {

        String userEmail = getCurrentUserEmail(authorizationHeader);

        // TODO: Implement ActivityService
        // For now, return empty list with a message
        return ResponseEntity.ok(ApiResponse.success(
                "Activities search will be available soon. Please check back later.",
                new ArrayList<>()
        ));
    }

    // Helper method to convert FlightResponse to SearchResult
    private List<SearchResult> convertFlightResponsesToSearchResults(List<FlightResponse> flightResponses) {
        if (flightResponses == null) {
            return new ArrayList<>();
        }

        return flightResponses.stream()
                .map(flight -> {
                    SearchResult result = new SearchResult();
                    result.setSearchType("FLIGHT");
                    result.setProvider(flight.getProvider());
                    result.setExternalId(flight.getFlightId());
                    result.setName(flight.getAirline() + " " + flight.getFlightNumber());
                    result.setDescription(String.format("%s → %s",
                            flight.getOriginCode(), flight.getDestinationCode()));
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
                    result.setIsAvailable(flight.getAvailableSeats() > 0);
                    result.setCreatedAt(java.time.LocalDateTime.now());
                    result.setUpdatedAt(java.time.LocalDateTime.now());
                    return result;
                })
                .collect(Collectors.toList());
    }

    // Helper method to convert HotelResponse to SearchResult
    private List<SearchResult> convertHotelResponsesToSearchResults(List<HotelResponse> hotelResponses) {
        if (hotelResponses == null) {
            return new ArrayList<>();
        }

        return hotelResponses.stream()
                .map(hotel -> {
                    SearchResult result = new SearchResult();
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

                    result.setIsAvailable(hotel.getAvailableRooms() != null && hotel.getAvailableRooms() > 0);
                    result.setCreatedAt(java.time.LocalDateTime.now());
                    result.setUpdatedAt(java.time.LocalDateTime.now());

                    return result;
                })
                .collect(Collectors.toList());
    }

    private String formatDuration(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%dh %dm", hours, mins);
    }
}