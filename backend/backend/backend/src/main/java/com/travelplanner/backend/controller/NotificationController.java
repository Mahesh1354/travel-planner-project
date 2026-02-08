package com.travelplanner.backend.controller;

import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.service.JwtService;
import com.travelplanner.backend.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JwtService jwtService;

    // Helper method to extract user email from JWT token
    private String getCurrentUserEmail(String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Remove "Bearer "
        return jwtService.getEmailFromToken(token);
    }

    // Notification operations
    @PostMapping
    public ResponseEntity<ApiResponse> createNotification(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody NotificationRequest request) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        NotificationResponse response = notificationService.createNotification(request, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Notification created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getUserNotifications(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        List<NotificationResponse> notifications =
                notificationService.getUserNotifications(userEmail, unreadOnly);

        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", notifications));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse> getUnreadCount(
            @RequestHeader("Authorization") String authorizationHeader) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        Long count = notificationService.getUnreadCount(userEmail);

        return ResponseEntity.ok(ApiResponse.success("Unread count retrieved successfully", count));
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse> markAsRead(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long notificationId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        NotificationResponse response = notificationService.markAsRead(notificationId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", response));
    }

    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse> markAllAsRead(
            @RequestHeader("Authorization") String authorizationHeader) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        notificationService.markAllAsRead(userEmail);

        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse> deleteNotification(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long notificationId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        notificationService.deleteNotification(notificationId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Notification deleted successfully", null));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteAllNotifications(
            @RequestHeader("Authorization") String authorizationHeader) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        notificationService.deleteAllNotifications(userEmail);

        return ResponseEntity.ok(ApiResponse.success("All notifications deleted successfully", null));
    }

    // Notification preference operations
    @PostMapping("/preferences")
    public ResponseEntity<ApiResponse> saveNotificationPreference(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody NotificationPreferenceRequest request) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        NotificationPreferenceResponse response =
                notificationService.saveNotificationPreference(request, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Notification preference saved successfully", response));
    }

    @GetMapping("/preferences")
    public ResponseEntity<ApiResponse> getUserNotificationPreferences(
            @RequestHeader("Authorization") String authorizationHeader) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        List<NotificationPreferenceResponse> preferences =
                notificationService.getUserNotificationPreferences(userEmail);

        return ResponseEntity.ok(ApiResponse.success("Notification preferences retrieved successfully", preferences));
    }

    @PutMapping("/preferences/{preferenceId}")
    public ResponseEntity<ApiResponse> updateNotificationPreference(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long preferenceId,
            @Valid @RequestBody NotificationPreferenceRequest request) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        NotificationPreferenceResponse response =
                notificationService.updateNotificationPreference(preferenceId, request, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Notification preference updated successfully", response));
    }

    @DeleteMapping("/preferences/{preferenceId}")
    public ResponseEntity<ApiResponse> deleteNotificationPreference(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long preferenceId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        notificationService.deleteNotificationPreference(preferenceId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Notification preference deleted successfully", null));
    }

    // Admin/System endpoints (might want to secure these differently in production)
    @PostMapping("/system/flight-updates")
    public ResponseEntity<ApiResponse> generateFlightUpdateNotifications(
            @RequestHeader("Authorization") String authorizationHeader) {

        // Verify admin role here in production
        notificationService.generateFlightUpdateNotifications();

        return ResponseEntity.ok(ApiResponse.success("Flight update notifications generated", null));
    }

    @PostMapping("/system/weather-alerts")
    public ResponseEntity<ApiResponse> generateWeatherAlertNotifications(
            @RequestHeader("Authorization") String authorizationHeader) {

        notificationService.generateWeatherAlertNotifications();

        return ResponseEntity.ok(ApiResponse.success("Weather alert notifications generated", null));
    }

    @PostMapping("/system/group-activities")
    public ResponseEntity<ApiResponse> generateGroupActivityNotifications(
            @RequestHeader("Authorization") String authorizationHeader) {

        notificationService.generateGroupActivityNotifications();

        return ResponseEntity.ok(ApiResponse.success("Group activity notifications generated", null));
    }

    @PostMapping("/system/send-pending")
    public ResponseEntity<ApiResponse> sendPendingNotifications(
            @RequestHeader("Authorization") String authorizationHeader) {

        notificationService.sendPendingNotifications();

        return ResponseEntity.ok(ApiResponse.success("Pending notifications sent", null));
    }
}
