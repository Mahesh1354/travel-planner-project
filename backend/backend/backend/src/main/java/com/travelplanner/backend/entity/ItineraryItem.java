package com.travelplanner.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "itinerary_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @NotNull(message = "Item date is required")
    @Column(name = "item_date", nullable = false)
    private LocalDateTime itemDate;

    @Column(name = "location")
    private String location;

    @Column(name = "item_type")
    private String itemType; // FLIGHT, ACCOMMODATION, ACTIVITY, FOOD, TRANSPORT, OTHER

    @Column(name = "cost")
    private Double cost;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "is_booked")
    private boolean isBooked = false;

    @Column(name = "booking_reference")
    private String bookingReference;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    // Pre-persist and pre-update methods
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}