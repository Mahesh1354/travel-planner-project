package com.travelplanner.backend.controller;
import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.service.JwtService;
import com.travelplanner.backend.service.OfflineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/offline")
public class OfflineController {

    @Autowired
    private OfflineService offlineService;

    @Autowired
    private JwtService jwtService;

    // Helper method to extract user email from JWT token
    private String getCurrentUserEmail(String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Remove "Bearer "
        return jwtService.getEmailFromToken(token);
    }

    // Cache operations
    @PostMapping("/cache")
    public ResponseEntity<ApiResponse> cacheData(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody OfflineDataRequest request) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        OfflineDataResponse response = offlineService.cacheData(request, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Data cached successfully", response));
    }

    @GetMapping("/cache/{cacheKey}")
    public ResponseEntity<ApiResponse> getCachedData(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String cacheKey) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        OfflineDataResponse response = offlineService.getCachedData(cacheKey, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Cached data retrieved successfully", response));
    }

    @GetMapping("/cache")
    public ResponseEntity<ApiResponse> getUserCachedData(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(required = false) String dataType) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        List<OfflineDataResponse> responses = offlineService.getUserCachedData(userEmail, dataType);

        return ResponseEntity.ok(ApiResponse.success("Cached data retrieved successfully", responses));
    }

    @DeleteMapping("/cache/{cacheKey}")
    public ResponseEntity<ApiResponse> deleteCachedData(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String cacheKey) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        offlineService.deleteCachedData(cacheKey, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Cached data deleted successfully", null));
    }

    @DeleteMapping("/cache")
    public ResponseEntity<ApiResponse> clearUserCache(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(required = false) String deviceId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        offlineService.clearUserCache(userEmail, deviceId);

        return ResponseEntity.ok(ApiResponse.success("Cache cleared successfully", null));
    }

    // Itinerary export operations
    @PostMapping("/export")
    public ResponseEntity<ApiResponse> exportItinerary(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody ItineraryExportRequest request) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        ItineraryExportResponse response = offlineService.exportItinerary(request, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Itinerary exported successfully", response));
    }

    @GetMapping("/exports/{exportId}/download")
    public ResponseEntity<byte[]> downloadExport(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String exportId,
            @RequestParam(required = false) String password) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        String fileContent = offlineService.getExportFile(exportId, userEmail, password);

        // Create response with file
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDispositionFormData("attachment", "itinerary.json");

        return new ResponseEntity<>(
                fileContent.getBytes(StandardCharsets.UTF_8),
                headers,
                HttpStatus.OK
        );
    }

    // Synchronization operations
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse> synchronizeData(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody SyncRequest request) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        SyncResponse response = offlineService.synchronizeData(request, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Synchronization completed", response));
    }

    @GetMapping("/sync/check")
    public ResponseEntity<ApiResponse> checkForUpdates(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String lastSyncToken) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        SyncResponse response = offlineService.checkForUpdates(deviceId, userEmail, lastSyncToken);

        return ResponseEntity.ok(ApiResponse.success("Updates checked successfully", response));
    }

    @PostMapping("/sync/resolve-conflicts")
    public ResponseEntity<ApiResponse> resolveConflicts(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody List<SyncResponse.ConflictItem> conflicts) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        List<SyncResponse.ConflictItem> resolved = offlineService.resolveConflicts(conflicts, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Conflicts resolved successfully", resolved));
    }

    // Helper endpoints
    @GetMapping("/generate-cache-key")
    public ResponseEntity<ApiResponse> generateCacheKey(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String dataType,
            @RequestParam(required = false) Long dataId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        String cacheKey = offlineService.generateCacheKey(dataType, dataId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Cache key generated successfully", cacheKey));
    }

    // System/admin endpoints
    @PostMapping("/system/cleanup")
    public ResponseEntity<ApiResponse> cleanupExpiredCache(
            @RequestHeader("Authorization") String authorizationHeader) {

        offlineService.cleanupExpiredCache();

        return ResponseEntity.ok(ApiResponse.success("Expired cache cleaned up successfully", null));
    }
}
