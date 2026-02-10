package com.travelplanner.backend.service;

import com.travelplanner.backend.dto.*;
import java.util.List;

public interface NotificationService {

    // Notification operations
    NotificationResponse createNotification(NotificationRequest request, String userEmail);
    List<NotificationResponse> getUserNotifications(String userEmail, boolean unreadOnly);
    NotificationResponse markAsRead(Long notificationId, String userEmail);
    void markAllAsRead(String userEmail);
    void deleteNotification(Long notificationId, String userEmail);
    void deleteAllNotifications(String userEmail);
    Long getUnreadCount(String userEmail);

    // Notification preference operations
    NotificationPreferenceResponse saveNotificationPreference(
            NotificationPreferenceRequest request, String userEmail);
    List<NotificationPreferenceResponse> getUserNotificationPreferences(String userEmail);
    NotificationPreferenceResponse updateNotificationPreference(
            Long preferenceId, NotificationPreferenceRequest request, String userEmail);
    void deleteNotificationPreference(Long preferenceId, String userEmail);

    // Automated notification generation
    void generateFlightUpdateNotifications();
    void generateWeatherAlertNotifications();
    void generateGroupActivityNotifications();

    // Notification sending
    void sendPendingNotifications();
    void sendNotificationToUser(NotificationRequest request, String userEmail);

    // Helper methods
    boolean shouldSendNotification(String userEmail, String notificationType);
    boolean isWithinQuietHours(String userEmail, String notificationType);
}
