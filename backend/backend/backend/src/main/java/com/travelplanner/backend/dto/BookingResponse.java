package com.travelplanner.backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String bookingType;
    private String externalId;
    private String providerName;
    private String itemName;
    private String description;
    private String location;
    private Double price;
    private String currency;
    private LocalDateTime bookingDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String bookingStatus;
    private String confirmationNumber;
    private String bookingDetails;
    private String cancellationPolicy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long tripId;
    private UserResponse user;
    private ItineraryItemResponse itineraryItem;

    // Constructor from Booking entity
    public BookingResponse(com.travelplanner.backend.entity.Booking booking) {
        this.id = booking.getId();
        this.bookingType = booking.getBookingType();
        this.externalId = booking.getExternalId();
        this.providerName = booking.getProviderName();
        this.itemName = booking.getItemName();
        this.description = booking.getDescription();
        this.location = booking.getLocation();
        this.price = booking.getPrice();
        this.currency = booking.getCurrency();
        this.bookingDate = booking.getBookingDate();
        this.startDate = booking.getStartDate();
        this.endDate = booking.getEndDate();
        this.bookingStatus = booking.getBookingStatus();
        this.confirmationNumber = booking.getConfirmationNumber();
        this.bookingDetails = booking.getBookingDetails();
        this.cancellationPolicy = booking.getCancellationPolicy();
        this.createdAt = booking.getCreatedAt();
        this.updatedAt = booking.getUpdatedAt();
        this.tripId = booking.getTrip().getId();
        this.user = new UserResponse(booking.getUser());
        if (booking.getItineraryItem() != null) {
            this.itineraryItem = new ItineraryItemResponse(booking.getItineraryItem());
        }
    }
}
