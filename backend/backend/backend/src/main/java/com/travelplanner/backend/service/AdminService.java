package com.travelplanner.backend.service;

import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.entity.AdminLog;

import java.util.List;
import java.util.Map;

public interface AdminService {

    // ==================== USER MANAGEMENT ====================

    /**
     * Manage user operations (activate, deactivate, delete)
     */
    UserResponse manageUser(UserManagementRequest request, String adminEmail);

    /**
     * Get paginated list of all users
     */
    PageResponse<UserResponse> getAllUsers(int page, int size, String sortBy, String sortOrder);

    /**
     * Search users with pagination
     */
    PageResponse<UserResponse> searchUsers(String query, String userType, int page, int size);

    /**
     * Change user role (USER, ADMIN, MODERATOR)
     */
    void changeUserRole(Long userId, String newRole, String adminEmail);

    /**
     * Bulk delete multiple users
     */
    void bulkDeleteUsers(List<Long> userIds, String adminEmail);

    /**
     * Bulk activate/deactivate users
     */
    void bulkActivateUsers(List<Long> userIds, boolean active, String adminEmail);

    // ==================== SYSTEM STATISTICS ====================

    /**
     * Get comprehensive system statistics
     */
    AdminStatsResponse getSystemStats();

    /**
     * Get detailed database statistics
     */
    DatabaseStatsDto getDatabaseStats();

    // ==================== DATA MANAGEMENT ====================

    /**
     * Clean up old data (notifications, logs, etc.)
     */
    void cleanupOldData(int daysToKeep);

    /**
     * Create database backup
     * @return backup ID
     */
    String backupDatabase();

    /**
     * Restore database from backup
     */
    void restoreDatabase(String backupId);

    /**
     * Export users as CSV
     * @return byte array of CSV data
     */
    byte[] exportUsersAsCsv();

    // ==================== SYSTEM CONFIGURATION ====================

    /**
     * Update system configuration
     */
    void updateSystemConfig(String key, String value, String description, String adminEmail);

    /**
     * Get all system configurations
     */
    List<SystemConfigDto> getSystemConfigs();

    /**
     * Get specific system configuration by key
     */
    SystemConfigDto getSystemConfig(String key);

    /**
     * Reset configuration to default value
     */
    void resetSystemConfig(String key, String adminEmail);

    // ==================== CACHE MANAGEMENT ====================

    /**
     * Get all cache names
     */
    List<String> getCacheNames();

    /**
     * Clear specific cache
     */
    void clearCache(String cacheName);

    // ==================== MONITORING & LOGS ====================

    /**
     * Get paginated admin logs
     */
    PageResponse<AdminLog> getAdminLogs(int page, int size);

    /**
     * Get paginated error logs
     */
    PageResponse<AdminLog> getErrorLogs(int page, int size);

    /**
     * Get audit trail for specific user
     */
    PageResponse<AdminLog> getUserAuditTrail(Long userId, int page, int size);

    // ==================== HEALTH CHECK ====================

    /**
     * Perform comprehensive system health check
     */
    HealthCheckDto systemHealthCheck();

    // ==================== AUDIT LOGGING (INTERNAL) ====================

    /**
     * Log admin action (internal use only)
     */
    void logAdminAction(String actionType, String details, String adminEmail,
                        Long affectedUserId, String affectedEntityType, Long affectedEntityId,
                        String status, String errorMessage, Long executionTimeMs);
}