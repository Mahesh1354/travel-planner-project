package com.travelplanner.backend.service;


import com.travelplanner.backend.dto.*;

import java.util.List;

public interface OfflineService {

    // Cache operations
    OfflineDataResponse cacheData(OfflineDataRequest request, String userEmail);
    OfflineDataResponse getCachedData(String cacheKey, String userEmail);
    List<OfflineDataResponse> getUserCachedData(String userEmail, String dataType);
    void deleteCachedData(String cacheKey, String userEmail);
    void clearUserCache(String userEmail, String deviceId);

    // Itinerary export
    ItineraryExportResponse exportItinerary(ItineraryExportRequest request, String userEmail);
    String getExportFile(String exportId, String userEmail, String password);

    // Synchronization
    SyncResponse synchronizeData(SyncRequest request, String userEmail);
    SyncResponse checkForUpdates(String deviceId, String userEmail, String lastSyncToken);

    // Offline change handling
    void applyOfflineChanges(List<SyncRequest.OfflineChange> changes, String userEmail);
    List<SyncResponse.ConflictItem> resolveConflicts(List<SyncResponse.ConflictItem> conflicts, String userEmail);

    // Helper methods
    String generateCacheKey(String dataType, Long dataId, String userEmail);
    boolean validateOfflineChange(SyncRequest.OfflineChange change);
    void cleanupExpiredCache();
}