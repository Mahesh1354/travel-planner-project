package com.travelplanner.backend.service.impl;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.entity.*;
import com.travelplanner.backend.exception.ResourceNotFoundException;
import com.travelplanner.backend.repository.*;
import com.travelplanner.backend.service.OfflineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OfflineServiceImpl implements OfflineService {

    @Autowired
    private OfflineCacheRepository offlineCacheRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ItineraryItemRepository itineraryItemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public OfflineDataResponse cacheData(OfflineDataRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Generate cache key
        String cacheKey = generateCacheKey(request.getDataType(), request.getDataId(), userEmail);

        // Get data to cache
        String cachedData = getDataForCache(request, user);

        // Check if cache already exists
        Optional<OfflineCache> existingCache = offlineCacheRepository
                .findByCacheKeyAndUserId(cacheKey, user.getId());

        OfflineCache cache;
        if (existingCache.isPresent()) {
            // Update existing cache
            cache = existingCache.get();
            cache.setCachedData(cachedData);
            cache.setVersion(cache.getVersion() + 1);
            cache.setDeviceId(request.getDeviceId());
            cache.setExpiresAt(LocalDateTime.now().plusDays(request.getExpirationDays()));
            cache.setDirty(false);
        } else {
            // Create new cache entry
            cache = new OfflineCache();
            cache.setCacheKey(cacheKey);
            cache.setDataType(request.getDataType());
            cache.setDataId(request.getDataId());
            cache.setUserId(user.getId());
            cache.setCachedData(cachedData);
            cache.setDeviceId(request.getDeviceId());
            cache.setExpiresAt(LocalDateTime.now().plusDays(request.getExpirationDays()));
            cache.setLastSynced(LocalDateTime.now());
        }

        OfflineCache savedCache = offlineCacheRepository.save(cache);
        return convertToResponse(savedCache);
    }

    @Override
    public OfflineDataResponse getCachedData(String cacheKey, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        OfflineCache cache = offlineCacheRepository
                .findByCacheKeyAndUserId(cacheKey, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cache not found"));

        // Check if cache is expired
        if (cache.isExpired()) {
            throw new IllegalArgumentException("Cached data has expired");
        }

        return convertToResponse(cache);
    }

    @Override
    public List<OfflineDataResponse> getUserCachedData(String userEmail, String dataType) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<OfflineCache> caches;
        if (dataType != null) {
            caches = offlineCacheRepository.findByDataTypeAndUserId(dataType, user.getId());
        } else {
            caches = offlineCacheRepository.findByUserId(user.getId());
        }

        // Filter out expired caches
        caches = caches.stream()
                .filter(cache -> !cache.isExpired())
                .collect(Collectors.toList());

        return caches.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCachedData(String cacheKey, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        OfflineCache cache = offlineCacheRepository
                .findByCacheKeyAndUserId(cacheKey, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cache not found"));

        offlineCacheRepository.delete(cache);
    }

    @Override
    @Transactional
    public void clearUserCache(String userEmail, String deviceId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (deviceId != null) {
            offlineCacheRepository.deleteByUserIdAndDeviceId(user.getId(), deviceId);
        } else {
            List<OfflineCache> userCaches = offlineCacheRepository.findByUserId(user.getId());
            offlineCacheRepository.deleteAll(userCaches);
        }
    }

    @Override
    @Transactional
    public ItineraryExportResponse exportItinerary(ItineraryExportRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        // Check if user has access to trip
        if (!hasAccessToTrip(trip, userEmail)) {
            throw new IllegalArgumentException("You don't have permission to export this trip");
        }

        // Build export data
        Map<String, Object> exportData = buildExportData(trip, request);

        try {
            String exportId = UUID.randomUUID().toString();
            String fileName = String.format("itinerary_%s_%s.%s",
                    trip.getTripName().replaceAll("\\s+", "_"),
                    LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")),
                    request.getFormat().toLowerCase());

            // In a real implementation, you would:
            // 1. Convert to requested format (JSON/PDF/CSV)
            // 2. Store the file (local filesystem, S3, etc.)
            // 3. Generate a download URL

            // For now, we'll create a mock response
            ItineraryExportResponse response = new ItineraryExportResponse();
            response.setExportId(exportId);
            response.setFileName(fileName);
            response.setFileType(request.getFormat());
            response.setFileSize((long) exportData.toString().length());
            response.setDownloadUrl("/api/offline/exports/" + exportId + "/download");
            response.setExportedAt(LocalDateTime.now());
            response.setRequiresPassword(request.getPassword() != null);

            // Cache the export data
            OfflineCache cache = new OfflineCache();
            cache.setCacheKey("EXPORT_" + exportId);
            cache.setDataType("EXPORT");
            cache.setDataId(trip.getId());
            cache.setUserId(user.getId());
            cache.setCachedData(objectMapper.writeValueAsString(exportData));
            cache.setDeviceId("EXPORT_SYSTEM");
            cache.setExpiresAt(LocalDateTime.now().plusHours(24)); // Exports expire in 24 hours

            offlineCacheRepository.save(cache);

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Failed to export itinerary", e);
        }
    }

    @Override
    public String getExportFile(String exportId, String userEmail, String password) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String cacheKey = "EXPORT_" + exportId;
        OfflineCache cache = offlineCacheRepository
                .findByCacheKeyAndUserId(cacheKey, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Export not found or expired"));

        if (cache.isExpired()) {
            throw new IllegalArgumentException("Export has expired");
        }

        // In real implementation, you would:
        // 1. Verify password if required
        // 2. Return the actual file content

        return cache.getCachedData(); // Return JSON for now
    }

    @Override
    @Transactional
    public SyncResponse synchronizeData(SyncRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SyncResponse response = new SyncResponse();
        response.setServerTime(LocalDateTime.now());
        response.setSyncToken(UUID.randomUUID().toString());

        List<SyncResponse.SyncItem> updates = new ArrayList<>();
        List<SyncResponse.ConflictItem> conflicts = new ArrayList<>();

        // Process cache items from client
        if (request.getCacheItems() != null) {
            for (SyncRequest.CacheItem clientItem : request.getCacheItems()) {
                try {
                    SyncResponse.SyncItem syncItem = processCacheItem(clientItem, user, request.getDeviceId());
                    if (syncItem != null) {
                        updates.add(syncItem);
                    }
                } catch (Exception e) {
                    // Handle conflict
                    SyncResponse.ConflictItem conflict = new SyncResponse.ConflictItem();
                    conflict.setCacheKey(clientItem.getCacheKey());
                    conflict.setDataType(clientItem.getDataType());
                    conflict.setDataId(clientItem.getDataId());
                    conflict.setClientData(clientItem.getData());
                    conflict.setServerData(getServerData(clientItem.getDataType(), clientItem.getDataId(), user));
                    conflict.setResolution("MANUAL");
                    conflicts.add(conflict);
                }
            }
        }

        // Apply offline changes
        if (request.getOfflineChanges() != null) {
            applyOfflineChanges(request.getOfflineChanges(), userEmail);
        }

        // Get updates for client
        if (request.isFullSync()) {
            updates.addAll(getAllUpdatesForUser(user, request.getDeviceId()));
        } else {
            updates.addAll(getUpdatesSinceLastSync(user, request.getDeviceId()));
        }

        response.setUpdates(updates);
        response.setConflicts(conflicts);
        response.setSuccess(true);
        response.setMessage("Synchronization completed");

        return response;
    }

    @Override
    public SyncResponse checkForUpdates(String deviceId, String userEmail, String lastSyncToken) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SyncResponse response = new SyncResponse();
        response.setServerTime(LocalDateTime.now());
        response.setSyncToken(UUID.randomUUID().toString());

        // Get cache items that need sync
        List<OfflineCache> cachesNeedingSync = offlineCacheRepository
                .findCacheNeedingSync(user.getId(), LocalDateTime.now());

        List<SyncResponse.SyncItem> updates = cachesNeedingSync.stream()
                .filter(cache -> deviceId == null || deviceId.equals(cache.getDeviceId()))
                .map(cache -> {
                    SyncResponse.SyncItem item = new SyncResponse.SyncItem();
                    item.setCacheKey(cache.getCacheKey());
                    item.setDataType(cache.getDataType());
                    item.setDataId(cache.getDataId());
                    item.setVersion(cache.getVersion());
                    item.setData(cache.getCachedData());
                    item.setAction(cache.isDirty() ? "UPDATE" : "NO_CHANGE");
                    return item;
                })
                .collect(Collectors.toList());

        response.setUpdates(updates);
        response.setSuccess(true);
        response.setMessage("Updates checked successfully");

        return response;
    }

    @Override
    @Transactional
    public void applyOfflineChanges(List<SyncRequest.OfflineChange> changes, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        for (SyncRequest.OfflineChange change : changes) {
            if (!validateOfflineChange(change)) {
                continue; // Skip invalid changes
            }

            try {
                switch (change.getChangeType()) {
                    case "CREATE":
                        createEntityFromChange(change, user);
                        break;
                    case "UPDATE":
                        updateEntityFromChange(change, user);
                        break;
                    case "DELETE":
                        deleteEntityFromChange(change, user);
                        break;
                }
            } catch (Exception e) {
                // Log error but continue with other changes
                System.err.println("Failed to apply offline change: " + e.getMessage());
            }
        }
    }

    @Override
    public List<SyncResponse.ConflictItem> resolveConflicts(List<SyncResponse.ConflictItem> conflicts, String userEmail) {
        // For now, implement simple conflict resolution: server wins
        // In a real application, you might implement more sophisticated resolution

        List<SyncResponse.ConflictItem> resolved = new ArrayList<>();

        for (SyncResponse.ConflictItem conflict : conflicts) {
            conflict.setResolution("SERVER_WINS");
            resolved.add(conflict);

            // Update cache with server data
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            Optional<OfflineCache> cache = offlineCacheRepository
                    .findByCacheKeyAndUserId(conflict.getCacheKey(), user.getId());

            if (cache.isPresent()) {
                OfflineCache cached = cache.get();
                cached.setCachedData(conflict.getServerData());
                cached.setDirty(false);
                cached.setLastSynced(LocalDateTime.now());
                offlineCacheRepository.save(cached);
            }
        }

        return resolved;
    }

    @Override
    public String generateCacheKey(String dataType, Long dataId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (dataId != null) {
            return String.format("%s_%d_%d", dataType, user.getId(), dataId);
        } else {
            return String.format("%s_%d_%s", dataType, user.getId(), UUID.randomUUID().toString());
        }
    }

    @Override
    public boolean validateOfflineChange(SyncRequest.OfflineChange change) {
        if (change.getChangeType() == null || change.getEntityType() == null) {
            return false;
        }

        if (change.getChangeType().equals("UPDATE") || change.getChangeType().equals("DELETE")) {
            return change.getEntityId() != null;
        }

        if (change.getChangeType().equals("CREATE")) {
            return change.getData() != null && !change.getData().isEmpty();
        }

        return true;
    }

    @Override
    @Transactional
    public void cleanupExpiredCache() {
        offlineCacheRepository.deleteExpiredCache(LocalDateTime.now());
    }

    // Helper methods
    private String getDataForCache(OfflineDataRequest request, User user) {
        try {
            Object data;

            switch (request.getDataType()) {
                case "TRIP":
                    if (request.getDataId() != null) {
                        Trip trip = tripRepository.findById(request.getDataId())
                                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

                        if (!hasAccessToTrip(trip, user.getEmail())) {
                            throw new IllegalArgumentException("No access to trip");
                        }

                        data = buildTripData(trip, request.isIncludeRelated());
                    } else {
                        List<Trip> trips = tripRepository.findByUserId(user.getId());
                        data = trips.stream()
                                .map(trip -> buildTripData(trip, request.isIncludeRelated()))
                                .collect(Collectors.toList());
                    }
                    break;

                case "ITINERARY":
                    if (request.getDataId() != null) {
                        Trip trip = tripRepository.findById(request.getDataId())
                                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

                        if (!hasAccessToTrip(trip, user.getEmail())) {
                            throw new IllegalArgumentException("No access to trip");
                        }

                        List<ItineraryItem> items = itineraryItemRepository.findByTrip(trip);
                        data = items.stream()
                                .map(item -> {
                                    Map<String, Object> itemData = new HashMap<>();
                                    itemData.put("id", item.getId());
                                    itemData.put("title", item.getTitle());
                                    itemData.put("description", item.getDescription());
                                    itemData.put("itemDate", item.getItemDate());
                                    itemData.put("location", item.getLocation());
                                    itemData.put("itemType", item.getItemType());
                                    itemData.put("cost", item.getCost());
                                    itemData.put("isBooked", item.isBooked());
                                    return itemData;
                                })
                                .collect(Collectors.toList());
                    } else {
                        data = Collections.emptyList();
                    }
                    break;

                default:
                    data = Collections.emptyMap();
            }

            return objectMapper.writeValueAsString(data);

        } catch (Exception e) {
            throw new RuntimeException("Failed to get data for cache", e);
        }
    }

    private Map<String, Object> buildTripData(Trip trip, boolean includeRelated) {
        Map<String, Object> tripData = new HashMap<>();
        tripData.put("id", trip.getId());
        tripData.put("tripName", trip.getTripName());
        tripData.put("description", trip.getDescription());
        tripData.put("startDate", trip.getStartDate());
        tripData.put("endDate", trip.getEndDate());
        tripData.put("destination", trip.getDestination());
        tripData.put("budget", trip.getBudget());
        tripData.put("notes", trip.getNotes());
        tripData.put("isPublic", trip.isPublic());
        tripData.put("createdAt", trip.getCreatedAt());

        if (includeRelated) {
            // Include itinerary items
            List<Map<String, Object>> items = itineraryItemRepository.findByTrip(trip).stream()
                    .map(item -> {
                        Map<String, Object> itemData = new HashMap<>();
                        itemData.put("id", item.getId());
                        itemData.put("title", item.getTitle());
                        itemData.put("description", item.getDescription());
                        itemData.put("itemDate", item.getItemDate());
                        itemData.put("location", item.getLocation());
                        return itemData;
                    })
                    .collect(Collectors.toList());
            tripData.put("itineraryItems", items);

            // Include bookings
            List<Map<String, Object>> bookings = bookingRepository.findByTrip(trip).stream()
                    .map(booking -> {
                        Map<String, Object> bookingData = new HashMap<>();
                        bookingData.put("id", booking.getId());
                        bookingData.put("itemName", booking.getItemName());
                        bookingData.put("bookingType", booking.getBookingType());
                        bookingData.put("price", booking.getPrice());
                        return bookingData;
                    })
                    .collect(Collectors.toList());
            tripData.put("bookings", bookings);
        }

        return tripData;
    }

    private Map<String, Object> buildExportData(Trip trip, ItineraryExportRequest request) {
        Map<String, Object> exportData = new HashMap<>();

        // Basic trip info
        exportData.put("trip", buildTripData(trip, true));

        // Include bookings if requested
        if (request.isIncludeBookings()) {
            List<Map<String, Object>> bookings = bookingRepository.findByTrip(trip).stream()
                    .map(booking -> {
                        Map<String, Object> bookingData = new HashMap<>();
                        bookingData.put("id", booking.getId());
                        bookingData.put("bookingType", booking.getBookingType());
                        bookingData.put("itemName", booking.getItemName());
                        bookingData.put("price", booking.getPrice());
                        bookingData.put("bookingDate", booking.getBookingDate());
                        bookingData.put("confirmationNumber", booking.getConfirmationNumber());
                        return bookingData;
                    })
                    .collect(Collectors.toList());
            exportData.put("bookings", bookings);
        }

        // Include budget if requested
        if (request.isIncludeBudget()) {
            // Budget data would come from BudgetService
            exportData.put("budget", Collections.singletonMap("totalBudget", trip.getBudget()));
        }

        // Include recommendations if requested
        if (request.isIncludeRecommendations()) {
            List<Map<String, Object>> recommendations = recommendationRepository
                    .findByLocationContainingIgnoreCase(trip.getDestination()).stream()
                    .limit(10)
                    .map(rec -> {
                        Map<String, Object> recData = new HashMap<>();
                        recData.put("title", rec.getTitle());
                        recData.put("description", rec.getDescription());
                        recData.put("category", rec.getCategory());
                        recData.put("rating", rec.getRating());
                        return recData;
                    })
                    .collect(Collectors.toList());
            exportData.put("recommendations", recommendations);
        }

        // Add metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("exportedAt", LocalDateTime.now());
        metadata.put("format", request.getFormat());
        metadata.put("tripId", trip.getId());
        metadata.put("tripName", trip.getTripName());
        exportData.put("metadata", metadata);

        return exportData;
    }

    private SyncResponse.SyncItem processCacheItem(SyncRequest.CacheItem clientItem, User user, String deviceId) {
        Optional<OfflineCache> existingCache = offlineCacheRepository
                .findByCacheKeyAndUserId(clientItem.getCacheKey(), user.getId());

        if (existingCache.isPresent()) {
            OfflineCache serverCache = existingCache.get();

            // Check for conflicts
            if (serverCache.getVersion() > clientItem.getVersion() && clientItem.isDirty()) {
                throw new RuntimeException("Version conflict");
            }

            // Update server cache
            serverCache.setCachedData(clientItem.getData());
            serverCache.setVersion(clientItem.getVersion() + 1);
            serverCache.setDeviceId(deviceId);
            serverCache.setDirty(false);
            serverCache.setLastSynced(LocalDateTime.now());

            offlineCacheRepository.save(serverCache);

            SyncResponse.SyncItem item = new SyncResponse.SyncItem();
            item.setCacheKey(serverCache.getCacheKey());
            item.setDataType(serverCache.getDataType());
            item.setDataId(serverCache.getDataId());
            item.setVersion(serverCache.getVersion());
            item.setData(serverCache.getCachedData());
            item.setAction("UPDATED");

            return item;
        }

        return null;
    }

    private String getServerData(String dataType, Long dataId, User user) {
        try {
            // This would fetch current data from server
            return "{}"; // Simplified for now
        } catch (Exception e) {
            return null;
        }
    }

    private List<SyncResponse.SyncItem> getAllUpdatesForUser(User user, String deviceId) {
        List<OfflineCache> caches = offlineCacheRepository.findByUserId(user.getId());

        return caches.stream()
                .filter(cache -> deviceId == null || deviceId.equals(cache.getDeviceId()))
                .map(cache -> {
                    SyncResponse.SyncItem item = new SyncResponse.SyncItem();
                    item.setCacheKey(cache.getCacheKey());
                    item.setDataType(cache.getDataType());
                    item.setDataId(cache.getDataId());
                    item.setVersion(cache.getVersion());
                    item.setData(cache.getCachedData());
                    item.setAction("UPDATE");
                    return item;
                })
                .collect(Collectors.toList());
    }

    private List<SyncResponse.SyncItem> getUpdatesSinceLastSync(User user, String deviceId) {
        // Simplified implementation - return all caches that need sync
        return getAllUpdatesForUser(user, deviceId);
    }

    private void createEntityFromChange(SyncRequest.OfflineChange change, User user) {
        // Implementation would create entity based on change data
        // This is simplified for the example
        switch (change.getEntityType()) {
            case "ITINERARY_ITEM":
                // Create itinerary item
                break;
            case "TRIP":
                // Create trip
                break;
            // Add other entity types as needed
        }
    }

    private void updateEntityFromChange(SyncRequest.OfflineChange change, User user) {
        // Implementation would update entity based on change data
    }

    private void deleteEntityFromChange(SyncRequest.OfflineChange change, User user) {
        // Implementation would delete entity
    }

    private boolean hasAccessToTrip(Trip trip, String userEmail) {
        if (trip.getUser().getEmail().equals(userEmail)) {
            return true;
        }

        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) return false;

        return trip.getCollaborators().stream()
                .anyMatch(c -> c.getUser().getEmail().equals(userEmail) && c.isAccepted());
    }

    private OfflineDataResponse convertToResponse(OfflineCache cache) {
        try {
            OfflineDataResponse response = new OfflineDataResponse();
            response.setCacheKey(cache.getCacheKey());
            response.setDataType(cache.getDataType());
            response.setDataId(cache.getDataId());
            response.setData(objectMapper.readValue(cache.getCachedData(), Object.class));
            response.setVersion(cache.getVersion());
            response.setLastSynced(cache.getLastSynced());
            response.setExpiresAt(cache.getExpiresAt());
            response.setDirty(cache.isDirty());
            response.setDeviceId(cache.getDeviceId());
            response.setCreatedAt(cache.getCreatedAt());
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert cache to response", e);
        }
    }
}
