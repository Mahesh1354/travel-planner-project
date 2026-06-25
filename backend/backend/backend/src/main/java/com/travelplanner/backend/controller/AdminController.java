package com.travelplanner.backend.controller;

import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.entity.AdminLog;
import com.travelplanner.backend.exception.ResourceNotFoundException;
import com.travelplanner.backend.service.AdminService;
import com.travelplanner.backend.util.SecurityUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @Autowired
    private SecurityUtils securityUtils;

    // ==================== DASHBOARD & STATS ====================

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getSystemStats() {
        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.info("Admin {} requested system stats", adminEmail);

        try {
            AdminStatsResponse stats = adminService.getSystemStats();
            return ResponseEntity.ok(
                    ApiResponse.success("System stats retrieved successfully", stats)
            );
        } catch (Exception e) {
            logger.error("Error retrieving system stats for admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve system stats"));
        }
    }

    @GetMapping("/stats/database")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getDatabaseStats() {
        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.info("Admin {} requested database stats", adminEmail);

        try {
            DatabaseStatsDto stats = adminService.getDatabaseStats();
            return ResponseEntity.ok(
                    ApiResponse.success("Database stats retrieved successfully", stats)
            );
        } catch (Exception e) {
            logger.error("Error retrieving database stats for admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve database stats"));
        }
    }

    // ==================== USER MANAGEMENT ====================

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllUsers(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") @Pattern(regexp = "asc|desc") String sortOrder) {

        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.info("Admin {} fetching users page {} with size {}", adminEmail, page, size);

        try {
            // This now returns PageResponse<UserResponse> which matches the service
            PageResponse<UserResponse> users = adminService.getAllUsers(page, size, sortBy, sortOrder);

            logger.debug("Retrieved {} users for admin {} (total: {})",
                    users.getContent().size(), adminEmail, users.getTotalElements());

            return ResponseEntity.ok(
                    ApiResponse.success("Users retrieved successfully", users)
            );
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid pagination parameters from admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching users for admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve users"));
        }
    }

    @GetMapping("/users/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> searchUsers(
            @RequestParam @NotBlank String query,
            @RequestParam(required = false) String userType,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.info("Admin {} searching users with query: '{}'", adminEmail, query);

        try {
            PageResponse<UserResponse> users = adminService.searchUsers(query, userType, page, size);

            logger.debug("Search returned {} users for admin {} (total: {})",
                    users.getContent().size(), adminEmail, users.getTotalElements());

            return ResponseEntity.ok(
                    ApiResponse.success("Users searched successfully", users)
            );
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid search parameters from admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error searching users for admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to search users"));
        }
    }

    @PostMapping("/users/manage")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> manageUser(
            @Valid @RequestBody UserManagementRequest request) {

        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.info("Admin {} managing user: {}", adminEmail, request.getUserId());

        try {
            UserResponse response = adminService.manageUser(request, adminEmail);

            logger.info("Admin {} successfully managed user: {}", adminEmail, request.getUserId());

            return ResponseEntity.ok(
                    ApiResponse.success("User managed successfully", response)
            );
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid user management request from admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (SecurityException e) {
            logger.warn("Security violation in user management by admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error managing user for admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to manage user"));
        }
    }

    @PutMapping("/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> changeUserRole(
            @PathVariable @Positive Long userId,
            @RequestParam @NotBlank String newRole) {

        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.info("Admin {} changing role of user {} to {}", adminEmail, userId, newRole);

        try {
            adminService.changeUserRole(userId, newRole, adminEmail);

            logger.info("Admin {} successfully changed role of user {} to {}",
                    adminEmail, userId, newRole);

            return ResponseEntity.ok(
                    ApiResponse.success("User role changed successfully", null)
            );
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid role change request from admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (SecurityException e) {
            logger.warn("Security violation in role change by admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error changing user role for admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to change user role"));
        }
    }

    @PostMapping("/users/bulk/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> bulkDeleteUsers(
            @RequestBody @Size(min = 1, max = 100) List<@Positive Long> userIds) {

        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.info("Admin {} bulk deleting {} users", adminEmail, userIds.size());

        try {
            adminService.bulkDeleteUsers(userIds, adminEmail);

            logger.info("Admin {} successfully deleted {} users", adminEmail, userIds.size());

            return ResponseEntity.ok(
                    ApiResponse.success("Users deleted successfully", null)
            );
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid bulk delete request from admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (SecurityException e) {
            logger.warn("Security violation in bulk delete by admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error bulk deleting users for admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete users"));
        }
    }

    @PostMapping("/users/bulk/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> bulkActivateUsers(
            @RequestBody @Size(min = 1, max = 100) List<@Positive Long> userIds,
            @RequestParam boolean active) {

        String adminEmail = securityUtils.getCurrentUserEmail();
        String action = active ? "activating" : "deactivating";
        logger.info("Admin {} {} {} users", adminEmail, action, userIds.size());

        try {
            adminService.bulkActivateUsers(userIds, active, adminEmail);

            logger.info("Admin {} successfully {} {} users", adminEmail, action, userIds.size());

            return ResponseEntity.ok(
                    ApiResponse.success("Users updated successfully", null)
            );
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid bulk activate request from admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (SecurityException e) {
            logger.warn("Security violation in bulk activate by admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error bulk activating users for admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update users"));
        }
    }

    // ==================== DATA MANAGEMENT ====================

    @PostMapping("/data/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> cleanupOldData(
            @RequestParam(defaultValue = "30") @Min(1) @Max(365) int daysToKeep) {

        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.info("Admin {} initiating data cleanup (keeping {} days)", adminEmail, daysToKeep);

        try {
            adminService.cleanupOldData(daysToKeep);

            logger.info("Admin {} completed data cleanup", adminEmail);

            return ResponseEntity.ok(
                    ApiResponse.success("Data cleanup completed successfully", null)
            );
        } catch (Exception e) {
            logger.error("Error during data cleanup by admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to cleanup data"));
        }
    }

    @PostMapping("/data/backup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> backupDatabase() {
        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.info("Admin {} initiating database backup", adminEmail);

        try {
            String backupId = adminService.backupDatabase();

            logger.info("Admin {} completed database backup with ID: {}", adminEmail, backupId);

            return ResponseEntity.ok(
                    ApiResponse.success("Database backup initiated successfully", backupId)
            );
        } catch (Exception e) {
            logger.error("Error during database backup by admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to backup database"));
        }
    }

    @PostMapping("/data/restore/{backupId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> restoreDatabase(
            @PathVariable @NotBlank String backupId) {

        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.warn("Admin {} initiating database restore from backup: {}", adminEmail, backupId);

        try {
            adminService.restoreDatabase(backupId);

            logger.warn("Admin {} completed database restore from backup: {}", adminEmail, backupId);

            return ResponseEntity.ok(
                    ApiResponse.success("Database restore initiated successfully", null)
            );
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid backup ID from admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error during database restore by admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to restore database"));
        }
    }

    @GetMapping("/data/export/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportUsersAsCsv() {
        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.info("Admin {} exporting users as CSV", adminEmail);

        try {
            byte[] csvData = adminService.exportUsersAsCsv();

            logger.info("Admin {} exported users as CSV successfully", adminEmail);

            return ResponseEntity.ok()
                    .header("Content-Type", "text/csv")
                    .header("Content-Disposition", "attachment; filename=users_export_" +
                            LocalDateTime.now().toLocalDate() + ".csv")
                    .body(csvData);
        } catch (Exception e) {
            logger.error("Error exporting users as CSV by admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error exporting users: " + e.getMessage()).getBytes());
        }
    }

    // ==================== SYSTEM CONFIGURATION ====================

    @GetMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getSystemConfigs() {
        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.debug("Admin {} fetching system configs", adminEmail);

        try {
            List<SystemConfigDto> configs = adminService.getSystemConfigs();

            return ResponseEntity.ok(
                    ApiResponse.success("System configs retrieved successfully", configs)
            );
        } catch (Exception e) {
            logger.error("Error fetching system configs for admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve system configs"));
        }
    }

    @GetMapping("/config/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getSystemConfig(
            @PathVariable @NotBlank String key) {

        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.debug("Admin {} fetching config for key: {}", adminEmail, key);

        try {
            SystemConfigDto config = adminService.getSystemConfig(key);

            return ResponseEntity.ok(
                    ApiResponse.success("System config retrieved successfully", config)
            );
        } catch (ResourceNotFoundException e) {
            logger.warn("Config key not found for admin {}: {}", adminEmail, key);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching config for admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve system config"));
        }
    }

    @PutMapping("/config/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateSystemConfig(
            @PathVariable @NotBlank String key,
            @RequestBody @Valid SystemConfigUpdateRequest request) {

        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.info("Admin {} updating config key: {} with value: {}",
                adminEmail, key, request.getValue());

        try {
            adminService.updateSystemConfig(key, request.getValue(), request.getDescription(), adminEmail);

            logger.info("Admin {} successfully updated config key: {}", adminEmail, key);

            return ResponseEntity.ok(
                    ApiResponse.success("System config updated successfully", null)
            );
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid config update by admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (SecurityException e) {
            logger.warn("Security violation in config update by admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating config for admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update system config"));
        }
    }

    @DeleteMapping("/config/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> resetSystemConfig(
            @PathVariable @NotBlank String key) {

        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.info("Admin {} resetting config key: {}", adminEmail, key);

        try {
            adminService.resetSystemConfig(key, adminEmail);

            logger.info("Admin {} successfully reset config key: {}", adminEmail, key);

            return ResponseEntity.ok(
                    ApiResponse.success("System config reset successfully", null)
            );
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid config reset by admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (SecurityException e) {
            logger.warn("Security violation in config reset by admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error resetting config for admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to reset system config"));
        }
    }

    // ==================== MONITORING & LOGS ====================

    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAdminLogs(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "50") @Min(1) @Max(500) int size) {

        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.info("Admin {} fetching admin logs", adminEmail);

        try {
            PageResponse<AdminLog> logs = adminService.getAdminLogs(page, size);

            return ResponseEntity.ok(
                    ApiResponse.success("Admin logs retrieved successfully", logs)
            );
        } catch (Exception e) {
            logger.error("Error fetching admin logs for admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve admin logs"));
        }
    }

    @GetMapping("/logs/errors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getErrorLogs(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "50") @Min(1) @Max(500) int size) {

        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.info("Admin {} fetching error logs", adminEmail);

        try {
            PageResponse<AdminLog> errorLogs = adminService.getErrorLogs(page, size);

            return ResponseEntity.ok(
                    ApiResponse.success("Error logs retrieved successfully", errorLogs)
            );
        } catch (Exception e) {
            logger.error("Error fetching error logs for admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve error logs"));
        }
    }

    @GetMapping("/logs/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getUserAuditTrail(
            @PathVariable @Positive Long userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "50") @Min(1) @Max(500) int size) {

        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.info("Admin {} fetching audit trail for user {}", adminEmail, userId);

        try {
            PageResponse<AdminLog> logs = adminService.getUserAuditTrail(userId, page, size);

            return ResponseEntity.ok(
                    ApiResponse.success("User audit trail retrieved successfully", logs)
            );
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid user ID for audit trail: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching audit trail for admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve audit trail"));
        }
    }

    // ==================== CACHE MANAGEMENT ====================

    @GetMapping("/cache")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getCacheNames() {
        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.debug("Admin {} fetching cache names", adminEmail);

        try {
            List<String> caches = adminService.getCacheNames();

            return ResponseEntity.ok(
                    ApiResponse.success("Cache names retrieved successfully", caches)
            );
        } catch (Exception e) {
            logger.error("Error fetching cache names for admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve cache names"));
        }
    }

    @DeleteMapping("/cache/{cacheName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> clearCache(
            @PathVariable @NotBlank String cacheName) {

        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.info("Admin {} clearing cache: {}", adminEmail, cacheName);

        try {
            adminService.clearCache(cacheName);

            logger.info("Admin {} cleared cache: {}", adminEmail, cacheName);

            return ResponseEntity.ok(
                    ApiResponse.success("Cache cleared successfully", null)
            );
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid cache name: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error clearing cache for admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to clear cache"));
        }
    }

    // ==================== HEALTH ====================

    @GetMapping("/health")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> systemHealthCheck() {
        String adminEmail = securityUtils.getCurrentUserEmail();
        logger.debug("Admin {} performing health check", adminEmail);

        try {
            HealthCheckDto health = adminService.systemHealthCheck();

            return ResponseEntity.ok(
                    ApiResponse.success("System health check completed", health)
            );
        } catch (Exception e) {
            logger.error("Error performing health check for admin {}: {}", adminEmail, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to perform health check"));
        }
    }

    @GetMapping("/health/public")
    public ResponseEntity<ApiResponse> publicHealthCheck() {
        logger.debug("Public health check requested");

        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().toString());
        health.put("service", "Travel Planner API");

        return ResponseEntity.ok(
                ApiResponse.success("Service is running", health)
        );
    }
}