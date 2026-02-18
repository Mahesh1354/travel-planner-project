package com.travelplanner.backend.service.external;

import com.travelplanner.backend.dto.external.FlightSearchRequest;
import com.travelplanner.backend.dto.external.FlightResponse;
import com.travelplanner.backend.dto.external.BookingRequest;
import com.travelplanner.backend.dto.external.BookingResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface FlightService {

    /**
     * Search for flights based on criteria
     * @param request flight search parameters
     * @return list of available flights
     */
    List<FlightResponse> searchFlights(FlightSearchRequest request);

    /**
     * Async search for better performance
     */
    CompletableFuture<List<FlightResponse>> searchFlightsAsync(FlightSearchRequest request);

    /**
     * Book a selected flight
     * @param request booking details
     * @return booking confirmation
     */
    BookingResponse bookFlight(BookingRequest request);

    /**
     * Cancel a booking
     * @param bookingReference provider's booking reference
     * @return cancellation confirmation
     */
    BookingResponse cancelBooking(String bookingReference);

    /**
     * Get booking details
     * @param bookingReference provider's booking reference
     * @return booking details
     */
    BookingResponse getBookingDetails(String bookingReference);
}