package com.travelplanner.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String notificationType; // FLIGHT_UPDATE, WEATHER_ALERT, GROUP_ACTIVITY, SYSTEM
    private String title;
    private String message;
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, URGENT
    private String relatedEntityType; // TRIP, BOOKING, ITINERARY_ITEM
    private Long relatedEntityId;
    private String metadata; // JSON string
    private LocalDateTime scheduledFor;
    private String sentVia = "IN_APP"; // IN_APP, EMAIL, SMS, PUSH
}