package com.travelplanner.backend.service.impl;

import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.entity.*;
import com.travelplanner.backend.exception.ResourceNotFoundException;
import com.travelplanner.backend.repository.*;
import com.travelplanner.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    @Transactional
    public NotificationResponse createNotification(NotificationRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user wants to receive this type of notification
        if (!shouldSendNotification(userEmail, request.getNotificationType())) {
            throw new IllegalArgumentException(
                    "User has disabled " + request.getNotificationType() + " notifications");
        }

        // Check quiet hours
        if (isWithinQuietHours(userEmail, request.getNotificationType())) {
            throw new IllegalArgumentException(
                    "Cannot send notification during quiet hours");
        }

        Notification notification = new Notification();
        notification.setNotificationType(request.getNotificationType());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setPriority(request.getPriority());
        notification.setRelatedEntityType(request.getRelatedEntityType());
        notification.setRelatedEntityId(request.getRelatedEntityId());
        notification.setMetadata(request.getMetadata());
        notification.setScheduledFor(request.getScheduledFor());
        notification.setSentVia(request.getSentVia());
        notification.setUser(user);

        // Send immediately if no scheduled time
        if (request.getScheduledFor() == null ||
                request.getScheduledFor().isBefore(LocalDateTime.now())) {
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
        }

        Notification savedNotification = notificationRepository.save(notification);
        return new NotificationResponse(savedNotification);
    }

    @Override
    public List<NotificationResponse> getUserNotifications(String userEmail, boolean unreadOnly) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Notification> notifications;
        if (unreadOnly) {
            notifications = notificationRepository.findByUserAndIsReadFalse(user);
        } else {
            notifications = notificationRepository.findByUser(user);
        }

        // Sort by creation date (newest first)
        notifications.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        return notifications.stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long notificationId, String userEmail) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        // Check if notification belongs to user
        if (!notification.getUser().getEmail().equals(userEmail)) {
            throw new IllegalArgumentException("Notification does not belong to user");
        }

        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notification = notificationRepository.save(notification);
        }

        return new NotificationResponse(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Notification> unreadNotifications = notificationRepository
                .findByUserAndIsReadFalse(user);

        LocalDateTime now = LocalDateTime.now();
        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
            notification.setReadAt(now);
        }

        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, String userEmail) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        // Check if notification belongs to user
        if (!notification.getUser().getEmail().equals(userEmail)) {
            throw new IllegalArgumentException("Notification does not belong to user");
        }

        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public void deleteAllNotifications(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Notification> notifications = notificationRepository.findByUser(user);
        notificationRepository.deleteAll(notifications);
    }

    @Override
    public Long getUnreadCount(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Override
    @Transactional
    public NotificationPreferenceResponse saveNotificationPreference(
            NotificationPreferenceRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if preference already exists
        Optional<NotificationPreference> existing = notificationPreferenceRepository
                .findByUserAndNotificationType(user, request.getNotificationType());

        if (existing.isPresent()) {
            // Update existing preference
            NotificationPreference preference = existing.get();
            preference.setEnabled(request.isEnabled());
            preference.setInApp(request.isInApp());
            preference.setEmail(request.isEmail());
            preference.setSms(request.isSms());
            preference.setPush(request.isPush());
            preference.setQuietHoursStart(request.getQuietHoursStart());
            preference.setQuietHoursEnd(request.getQuietHoursEnd());

            NotificationPreference updated = notificationPreferenceRepository.save(preference);
            return new NotificationPreferenceResponse(updated);
        }

        // Create new preference
        NotificationPreference preference = new NotificationPreference();
        preference.setNotificationType(request.getNotificationType());
        preference.setEnabled(request.isEnabled());
        preference.setInApp(request.isInApp());
        preference.setEmail(request.isEmail());
        preference.setSms(request.isSms());
        preference.setPush(request.isPush());
        preference.setQuietHoursStart(request.getQuietHoursStart());
        preference.setQuietHoursEnd(request.getQuietHoursEnd());
        preference.setUser(user);

        NotificationPreference saved = notificationPreferenceRepository.save(preference);
        return new NotificationPreferenceResponse(saved);
    }

    @Override
    public List<NotificationPreferenceResponse> getUserNotificationPreferences(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<NotificationPreference> preferences = notificationPreferenceRepository
                .findByUser(user);

        // If user has no preferences, create default ones
        if (preferences.isEmpty()) {
            preferences = createDefaultPreferences(user);
        }

        return preferences.stream()
                .map(NotificationPreferenceResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationPreferenceResponse updateNotificationPreference(
            Long preferenceId, NotificationPreferenceRequest request, String userEmail) {
        NotificationPreference preference = notificationPreferenceRepository.findById(preferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Preference not found"));

        // Check if preference belongs to user
        if (!preference.getUser().getEmail().equals(userEmail)) {
            throw new IllegalArgumentException("Preference does not belong to user");
        }

        preference.setEnabled(request.isEnabled());
        preference.setInApp(request.isInApp());
        preference.setEmail(request.isEmail());
        preference.setSms(request.isSms());
        preference.setPush(request.isPush());
        preference.setQuietHoursStart(request.getQuietHoursStart());
        preference.setQuietHoursEnd(request.getQuietHoursEnd());

        NotificationPreference updated = notificationPreferenceRepository.save(preference);
        return new NotificationPreferenceResponse(updated);
    }

    @Override
    @Transactional
    public void deleteNotificationPreference(Long preferenceId, String userEmail) {
        NotificationPreference preference = notificationPreferenceRepository.findById(preferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Preference not found"));

        // Check if preference belongs to user
        if (!preference.getUser().getEmail().equals(userEmail)) {
            throw new IllegalArgumentException("Preference does not belong to user");
        }

        notificationPreferenceRepository.delete(preference);
    }

    @Override
    @Transactional
    public void generateFlightUpdateNotifications() {
        LocalDateTime twentyFourHoursFromNow = LocalDateTime.now().plusHours(24);

        // Find flights departing in the next 24 hours
        List<Booking> upcomingFlights = bookingRepository.findByBookingType("FLIGHT");

        for (Booking flight : upcomingFlights) {
            if (flight.getStartDate() != null &&
                    flight.getStartDate().isBefore(twentyFourHoursFromNow)) {

                // Check for delays or updates (mock logic)
                boolean hasDelay = Math.random() < 0.1; // 10% chance of delay

                if (hasDelay) {
                    String delayMessage = String.format(
                            "Your flight %s from %s to %s has been delayed by 30 minutes. New departure time: %s",
                            flight.getItemName(),
                            extractLocationInfo(flight.getLocation(), "origin"),
                            extractLocationInfo(flight.getLocation(), "destination"),
                            flight.getStartDate().plusMinutes(30)
                    );

                    NotificationRequest request = new NotificationRequest();
                    request.setNotificationType("FLIGHT_UPDATE");
                    request.setTitle("Flight Delay Notification");
                    request.setMessage(delayMessage);
                    request.setPriority("HIGH");
                    request.setRelatedEntityType("BOOKING");
                    request.setRelatedEntityId(flight.getId());
                    request.setMetadata(String.format(
                            "{\"originalTime\":\"%s\",\"newTime\":\"%s\",\"delayMinutes\":30}",
                            flight.getStartDate(),
                            flight.getStartDate().plusMinutes(30)
                    ));

                    sendNotificationToUser(request, flight.getUser().getEmail());
                }
            }
        }
    }

    @Override
    @Transactional
    public void generateWeatherAlertNotifications() {
        // Get trips happening in the next 3 days
        LocalDateTime threeDaysFromNow = LocalDateTime.now().plusDays(3);

        List<Trip> upcomingTrips = tripRepository.findAll().stream()
                .filter(trip -> trip.getStartDate() != null &&
                        !trip.getStartDate().isAfter(threeDaysFromNow.toLocalDate()))
                .collect(Collectors.toList());

        for (Trip trip : upcomingTrips) {
            // Mock weather check - 20% chance of bad weather
            boolean badWeather = Math.random() < 0.2;

            if (badWeather && trip.getDestination() != null) {
                String weatherMessage = String.format(
                        "Weather alert for %s: Expect heavy rain during your trip dates (%s to %s). " +
                                "Consider packing rain gear and checking indoor activity options.",
                        trip.getDestination(),
                        trip.getStartDate(),
                        trip.getEndDate()
                );

                // Send to trip owner
                NotificationRequest request = new NotificationRequest();
                request.setNotificationType("WEATHER_ALERT");
                request.setTitle("Weather Alert for Your Trip");
                request.setMessage(weatherMessage);
                request.setPriority("MEDIUM");
                request.setRelatedEntityType("TRIP");
                request.setRelatedEntityId(trip.getId());
                request.setMetadata(String.format(
                        "{\"destination\":\"%s\",\"severity\":\"moderate\",\"condition\":\"heavy rain\"}",
                        trip.getDestination()
                ));

                sendNotificationToUser(request, trip.getUser().getEmail());

                // Send to collaborators
                for (TripCollaborator collaborator : trip.getCollaborators()) {
                    if (collaborator.isAccepted()) {
                        sendNotificationToUser(request, collaborator.getUser().getEmail());
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void generateGroupActivityNotifications() {
        // Find trips with collaborators that have upcoming activities
        List<Trip> collaborativeTrips = tripRepository.findAll().stream()
                .filter(trip -> !trip.getCollaborators().isEmpty())
                .collect(Collectors.toList());

        for (Trip trip : collaborativeTrips) {
            // Check for itinerary items added in the last hour
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

            List<com.travelplanner.backend.entity.ItineraryItem> recentItems =
                    trip.getItineraryItems().stream()
                            .filter(item -> item.getCreatedAt() != null &&
                                    item.getCreatedAt().isAfter(oneHourAgo))
                            .collect(Collectors.toList());

            for (com.travelplanner.backend.entity.ItineraryItem item : recentItems) {
                String activityMessage = String.format(
                        "New activity added to trip '%s': %s on %s at %s",
                        trip.getTripName(),
                        item.getTitle(),
                        item.getItemDate().toLocalDate(),
                        item.getItemDate().toLocalTime()
                );

                // Send to all collaborators except the one who added it
                for (TripCollaborator collaborator : trip.getCollaborators()) {
                    if (collaborator.isAccepted() &&
                            !collaborator.getUser().getId().equals(item.getTrip().getUser().getId())) {

                        NotificationRequest request = new NotificationRequest();
                        request.setNotificationType("GROUP_ACTIVITY");
                        request.setTitle("New Activity Added to Shared Trip");
                        request.setMessage(activityMessage);
                        request.setPriority("LOW");
                        request.setRelatedEntityType("ITINERARY_ITEM");
                        request.setRelatedEntityId(item.getId());
                        request.setMetadata(String.format(
                                "{\"addedBy\":\"%s\",\"tripId\":%d}",
                                item.getTrip().getUser().getFullName(),
                                trip.getId()
                        ));

                        sendNotificationToUser(request, collaborator.getUser().getEmail());
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void sendPendingNotifications() {
        LocalDateTime now = LocalDateTime.now();
        List<Notification> pendingNotifications = notificationRepository
                .findNotificationsToSend(now);

        for (Notification notification : pendingNotifications) {
            if (!notification.isSent() &&
                    shouldSendNotification(notification.getUser().getEmail(),
                            notification.getNotificationType())) {

                // Send notification (mock implementation)
                notification.setSent(true);
                notification.setSentAt(now);
                notificationRepository.save(notification);

                // In real implementation, you would:
                // 1. Send in-app notification
                // 2. Send email if enabled
                // 3. Send SMS if enabled
                // 4. Send push notification if enabled
            }
        }
    }

    @Override
    @Transactional
    public void sendNotificationToUser(NotificationRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check preferences
        if (!shouldSendNotification(userEmail, request.getNotificationType())) {
            return;
        }

        // Check quiet hours
        if (isWithinQuietHours(userEmail, request.getNotificationType())) {
            // Schedule for after quiet hours
            request.setScheduledFor(getNextAllowedTime(userEmail, request.getNotificationType()));
        }

        Notification notification = new Notification();
        notification.setNotificationType(request.getNotificationType());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setPriority(request.getPriority());
        notification.setRelatedEntityType(request.getRelatedEntityType());
        notification.setRelatedEntityId(request.getRelatedEntityId());
        notification.setMetadata(request.getMetadata());
        notification.setScheduledFor(request.getScheduledFor());
        notification.setSentVia(request.getSentVia());
        notification.setUser(user);

        // Send immediately if no scheduled time and not in quiet hours
        if (request.getScheduledFor() == null &&
                !isWithinQuietHours(userEmail, request.getNotificationType())) {
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
        }

        notificationRepository.save(notification);
    }

    @Override
    public boolean shouldSendNotification(String userEmail, String notificationType) {
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) return false;

        Optional<NotificationPreference> preference = notificationPreferenceRepository
                .findByUserAndNotificationType(user, notificationType);

        // If no preference exists, create default and return true
        if (preference.isEmpty()) {
            createDefaultPreferenceForType(user, notificationType);
            return true;
        }

        return preference.get().isEnabled();
    }

    @Override
    public boolean isWithinQuietHours(String userEmail, String notificationType) {
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) return false;

        Optional<NotificationPreference> preference = notificationPreferenceRepository
                .findByUserAndNotificationType(user, notificationType);

        if (preference.isEmpty() ||
                preference.get().getQuietHoursStart() == null ||
                preference.get().getQuietHoursEnd() == null) {
            return false;
        }

        int currentHour = LocalTime.now().getHour();
        int startHour = preference.get().getQuietHoursStart();
        int endHour = preference.get().getQuietHoursEnd();

        // Handle overnight quiet hours (e.g., 22 to 7)
        if (startHour > endHour) {
            return currentHour >= startHour || currentHour < endHour;
        } else {
            return currentHour >= startHour && currentHour < endHour;
        }
    }

    // Helper methods
    private List<NotificationPreference> createDefaultPreferences(User user) {
        List<NotificationPreference> preferences = new ArrayList<>();

        String[] notificationTypes = {
                "FLIGHT_UPDATE", "WEATHER_ALERT", "GROUP_ACTIVITY", "SYSTEM"
        };

        for (String type : notificationTypes) {
            NotificationPreference preference = new NotificationPreference();
            preference.setNotificationType(type);
            preference.setEnabled(true);
            preference.setInApp(true);
            preference.setEmail(type.equals("FLIGHT_UPDATE") || type.equals("WEATHER_ALERT"));
            preference.setSms(false);
            preference.setPush(true);
            preference.setUser(user);

            // Set quiet hours for non-urgent notifications
            if (type.equals("GROUP_ACTIVITY") || type.equals("SYSTEM")) {
                preference.setQuietHoursStart(22); // 10 PM
                preference.setQuietHoursEnd(7);    // 7 AM
            }

            preferences.add(preference);
        }

        return notificationPreferenceRepository.saveAll(preferences);
    }

    private void createDefaultPreferenceForType(User user, String notificationType) {
        NotificationPreference preference = new NotificationPreference();
        preference.setNotificationType(notificationType);
        preference.setEnabled(true);
        preference.setInApp(true);
        preference.setEmail(true);
        preference.setSms(false);
        preference.setPush(true);
        preference.setUser(user);

        notificationPreferenceRepository.save(preference);
    }

    private LocalDateTime getNextAllowedTime(String userEmail, String notificationType) {
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) return LocalDateTime.now();

        Optional<NotificationPreference> preference = notificationPreferenceRepository
                .findByUserAndNotificationType(user, notificationType);

        if (preference.isEmpty() || preference.get().getQuietHoursEnd() == null) {
            return LocalDateTime.now();
        }

        LocalDateTime now = LocalDateTime.now();
        int endHour = preference.get().getQuietHoursEnd();

        // If current hour is before end hour, schedule for end hour today
        // Otherwise, schedule for end hour tomorrow
        if (now.getHour() < endHour) {
            return now.withHour(endHour).withMinute(0).withSecond(0);
        } else {
            return now.plusDays(1).withHour(endHour).withMinute(0).withSecond(0);
        }
    }

    private String extractLocationInfo(String location, String type) {
        if (location == null) return "Unknown";

        if (location.contains("→")) {
            String[] parts = location.split("→");
            if (type.equals("origin") && parts.length > 0) {
                return parts[0].trim();
            } else if (type.equals("destination") && parts.length > 1) {
                return parts[1].trim();
            }
        }

        return location;
    }
}
