package com.travelplanner.backend.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_type", nullable = false)
    private String bookingType; // FLIGHT, HOTEL, ACTIVITY

    @Column(name = "external_id")
    private String externalId; // ID from external API

    @Column(name = "provider_name")
    private String providerName; // MockAir, MockHotels, MockActivities

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "location")
    private String location;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "currency")
    private String currency = "USD";

    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "booking_status")
    private String bookingStatus = "CONFIRMED"; // CONFIRMED, PENDING, CANCELLED

    @Column(name = "confirmation_number")
    private String confirmationNumber;

    @Column(name = "booking_details", length = 2000)
    private String bookingDetails; // JSON string with details

    @Column(name = "cancellation_policy", length = 1000)
    private String cancellationPolicy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "itinerary_item_id")
    private ItineraryItem itineraryItem;

    // Pre-persist and pre-update methods
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        bookingDate = LocalDateTime.now();

        // Generate confirmation number if not provided
        if (confirmationNumber == null) {
            confirmationNumber = "MOCK-" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}