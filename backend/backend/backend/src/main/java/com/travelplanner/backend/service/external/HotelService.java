package com.travelplanner.backend.service.external;

import com.travelplanner.backend.dto.external.HotelSearchRequest;
import com.travelplanner.backend.dto.external.HotelResponse;
import com.travelplanner.backend.dto.external.HotelBookingRequest;
import com.travelplanner.backend.dto.external.HotelBookingResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface HotelService {

    /**
     * Search for hotels based on criteria
     * @param request hotel search parameters
     * @return list of available hotels
     */
    List<HotelResponse> searchHotels(HotelSearchRequest request);

    /**
     * Async search for better performance
     */
    CompletableFuture<List<HotelResponse>> searchHotelsAsync(HotelSearchRequest request);

    /**
     * Get hotel details by ID
     * @param hotelId provider's hotel ID
     * @return detailed hotel information
     */
    HotelResponse getHotelDetails(String hotelId);

    /**
     * Check room availability
     * @param hotelId provider's hotel ID
     * @param checkInDate check-in date
     * @param checkOutDate check-out date
     * @return available room types and rates
     */
    List<HotelResponse.RoomOffer> checkAvailability(String hotelId,
                                                    LocalDate checkInDate,
                                                    LocalDate checkOutDate);

    /**
     * Book a hotel
     * @param request booking details
     * @return booking confirmation
     */
    HotelBookingResponse bookHotel(HotelBookingRequest request);

    /**
     * Cancel a booking
     * @param bookingReference provider's booking reference
     * @return cancellation confirmation
     */
    HotelBookingResponse cancelBooking(String bookingReference);

    /**
     * Get booking details
     * @param bookingReference provider's booking reference
     * @return booking details
     */
    HotelBookingResponse getBookingDetails(String bookingReference);
}