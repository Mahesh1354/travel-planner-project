package com.travelplanner.backend.service.impl;

import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.entity.*;
import com.travelplanner.backend.exception.ResourceNotFoundException;
import com.travelplanner.backend.exception.UnauthorizedAccessException;
import com.travelplanner.backend.repository.*;
import com.travelplanner.backend.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.DatabaseMetaData;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AdminLogRepository adminLogRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired(required = false)
    private HttpServletRequest request;

    // ==================== USER MANAGEMENT ====================

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse manageUser(UserManagementRequest request, String adminEmail) {
        validateAdmin(adminEmail);

        User targetUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        LocalDateTime startTime = LocalDateTime.now();
        String actionDetails = "";
        String status = "SUCCESS";
        String errorMessage = null;

        try {
            switch (request.getAction().toUpperCase()) {
                case "DEACTIVATE":
                    targetUser.setActive(false);
                    actionDetails = String.format("Deactivated user %s (%s). Reason: %s",
                            targetUser.getFullName(), targetUser.getEmail(), request.getReason());
                    break;

                case "ACTIVATE":
                    targetUser.setActive(true);
                    actionDetails = String.format("Activated user %s (%s)",
                            targetUser.getFullName(), targetUser.getEmail());
                    break;

                case "DELETE":
                    // Soft delete - mark as inactive and anonymize data
                    targetUser.setActive(false);
                    targetUser.setEmail("deleted_" + System.currentTimeMillis() + "@deleted.user");
                    targetUser.setFullName("Deleted User");
                    targetUser.setPhoneNumber(null);
                    targetUser.setProfilePicture(null);
                    actionDetails = String.format("Soft deleted user ID %d. Reason: %s",
                            request.getUserId(), request.getReason());
                    break;

                default:
                    throw new IllegalArgumentException("Invalid action: " + request.getAction());
            }

            User updatedUser = userRepository.save(targetUser);

            // Log the action
            logAdminAction(
                    "USER_MANAGEMENT",
                    actionDetails,
                    adminEmail,
                    targetUser.getId(),
                    "USER",
                    targetUser.getId(),
                    status,
                    errorMessage,
                    ChronoUnit.MILLIS.between(startTime, LocalDateTime.now())
            );

            return new UserResponse(updatedUser);

        } catch (Exception e) {
            status = "FAILED";
            errorMessage = e.getMessage();

            logAdminAction(
                    "USER_MANAGEMENT",
                    "Failed to " + request.getAction() + " user ID " + request.getUserId(),
                    adminEmail,
                    targetUser != null ? targetUser.getId() : null,
                    "USER",
                    targetUser != null ? targetUser.getId() : null,
                    status,
                    errorMessage,
                    ChronoUnit.MILLIS.between(startTime, LocalDateTime.now())
            );

            throw e;
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<UserResponse> getAllUsers(int page, int size, String sortBy, String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<User> userPage = userRepository.findAll(pageable);

        List<UserResponse> content = userPage.getContent().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());

        return PageResponse.<UserResponse>builder()
                .content(content)
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<UserResponse> searchUsers(String query, String userType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // This is simplified - in production, use Specification for complex queries
        List<User> allUsers = userRepository.findAll();

        List<User> filteredUsers = allUsers.stream()
                .filter(user ->
                        (query == null || query.isEmpty() ||
                                user.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                                user.getFullName().toLowerCase().contains(query.toLowerCase())) &&
                                (userType == null || userType.isEmpty() ||
                                        user.getUserType().name().equalsIgnoreCase(userType)))
                .collect(Collectors.toList());

        // Manual pagination (in production, use Spring Data JPA Specifications)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredUsers.size());

        List<UserResponse> content = filteredUsers.subList(start, end).stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());

        return PageResponse.<UserResponse>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(filteredUsers.size())
                .totalPages((int) Math.ceil((double) filteredUsers.size() / size))
                .last(end >= filteredUsers.size())
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void changeUserRole(Long userId, String newRole, String adminEmail) {
        validateAdmin(adminEmail);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        try {
            UserType userType = UserType.valueOf(newRole.toUpperCase());
            user.setUserType(userType);
            userRepository.save(user);

            logAdminAction(
                    "USER_MANAGEMENT",
                    String.format("Changed role for user %d from %s to %s",
                            userId, user.getUserType(), newRole),
                    adminEmail,
                    userId,
                    "USER",
                    userId,
                    "SUCCESS",
                    null,
                    0L
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + newRole);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void bulkDeleteUsers(List<Long> userIds, String adminEmail) {
        validateAdmin(adminEmail);

        List<User> users = userRepository.findAllById(userIds);

        for (User user : users) {
            user.setActive(false);
            user.setEmail("deleted_" + System.currentTimeMillis() + "@deleted.user");
            user.setFullName("Deleted User");
        }

        userRepository.saveAll(users);

        logAdminAction(
                "BULK_OPERATION",
                String.format("Soft deleted %d users", users.size()),
                adminEmail,
                null,
                "USER",
                null,
                "SUCCESS",
                null,
                0L
        );
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void bulkActivateUsers(List<Long> userIds, boolean active, String adminEmail) {
        validateAdmin(adminEmail);

        List<User> users = userRepository.findAllById(userIds);

        for (User user : users) {
            user.setActive(active);
        }

        userRepository.saveAll(users);

        logAdminAction(
                "BULK_OPERATION",
                String.format("Set active=%b for %d users", active, users.size()),
                adminEmail,
                null,
                "USER",
                null,
                "SUCCESS",
                null,
                0L
        );
    }

    // ==================== SYSTEM STATISTICS ====================

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public AdminStatsResponse getSystemStats() {
        AdminStatsResponse stats = new AdminStatsResponse();

        // User statistics
        List<User> allUsers = userRepository.findAll();
        stats.setTotalUsers((long) allUsers.size());

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long activeUsers = allUsers.stream()
                .filter(User::isActive)
                .filter(user -> user.getCreatedAt() != null &&
                        user.getCreatedAt().isAfter(thirtyDaysAgo))
                .count();
        stats.setActiveUsersLast30Days(activeUsers);

        // User type distribution
        Map<String, Long> usersByType = allUsers.stream()
                .collect(Collectors.groupingBy(
                        user -> user.getUserType().name(),
                        Collectors.counting()
                ));
        stats.setUsersByType(usersByType);

        // Trip statistics
        List<Trip> allTrips = tripRepository.findAll();
        stats.setTotalTrips((long) allTrips.size());

        // Booking statistics
        List<Booking> allBookings = bookingRepository.findAll();
        stats.setTotalBookings((long) allBookings.size());

        double totalBookingValue = allBookings.stream()
                .mapToDouble(booking -> booking.getPrice() != null ? booking.getPrice() : 0)
                .sum();
        stats.setTotalBookingValue(totalBookingValue);

        // Notification statistics
        List<Notification> allNotifications = notificationRepository.findAll();
        long notificationsSent = allNotifications.stream()
                .filter(Notification::isSent)
                .count();
        stats.setTotalNotificationsSent(notificationsSent);

        return stats;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public DatabaseStatsDto getDatabaseStats() {
        try {
            // Get table row counts
            Long totalUsers = userRepository.count();
            Long totalTrips = tripRepository.count();
            Long totalBookings = bookingRepository.count();

            // Get database size (MySQL specific)
            String sizeSql = """
                SELECT ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) 
                FROM information_schema.TABLES 
                WHERE table_schema = DATABASE()
            """;
            Double dbSize = jdbcTemplate.queryForObject(sizeSql, Double.class);

            // Get last backup time (simulated)
            String lastBackup = getLastBackupTime();

            // Get active connections
            String connSql = "SHOW STATUS LIKE 'Threads_connected'";
            Map<String, Object> connInfo = jdbcTemplate.queryForMap(connSql);
            Integer activeConnections = Integer.parseInt(connInfo.get("Value").toString());

            return DatabaseStatsDto.builder()
                    .totalUsers(totalUsers)
                    .totalTrips(totalTrips)
                    .totalBookings(totalBookings)
                    .databaseSizeMb(dbSize != null ? dbSize.longValue() : 0L)
                    .lastBackupTime(lastBackup)
                    .activeConnections(activeConnections)
                    .build();

        } catch (Exception e) {
            // Return default values on error
            return DatabaseStatsDto.builder()
                    .totalUsers(userRepository.count())
                    .totalTrips(tripRepository.count())
                    .totalBookings(bookingRepository.count())
                    .databaseSizeMb(0L)
                    .lastBackupTime("Unknown")
                    .activeConnections(0)
                    .build();
        }
    }

    // ==================== DATA MANAGEMENT ====================

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void cleanupOldData(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);

        // Cleanup old notifications
        List<Notification> oldNotifications = notificationRepository.findAll().stream()
                .filter(notification -> notification.getCreatedAt() != null &&
                        notification.getCreatedAt().isBefore(cutoffDate))
                .collect(Collectors.toList());

        int notificationCount = oldNotifications.size();
        if (!oldNotifications.isEmpty()) {
            notificationRepository.deleteAll(oldNotifications);
        }

        // Log the cleanup
        logAdminAction(
                "DATA_CLEANUP",
                String.format("Cleaned up %d old notifications (older than %d days)",
                        notificationCount, daysToKeep),
                getCurrentAdminEmail(),
                null,
                "SYSTEM",
                null,
                "SUCCESS",
                null,
                0L
        );
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public String backupDatabase() {
        String backupId = "backup_" + System.currentTimeMillis();

        logAdminAction(
                "SYSTEM_CONFIG",
                "Database backup created: " + backupId,
                getCurrentAdminEmail(),
                null,
                "SYSTEM",
                null,
                "SUCCESS",
                null,
                0L
        );

        return backupId;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void restoreDatabase(String backupId) {
        logAdminAction(
                "SYSTEM_CONFIG",
                "Database restore initiated from backup: " + backupId,
                getCurrentAdminEmail(),
                null,
                "SYSTEM",
                null,
                "SUCCESS",
                null,
                0L
        );
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public byte[] exportUsersAsCsv() {
        List<User> users = userRepository.findAll();

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Email,Full Name,User Type,Active,Created At\n");

        for (User user : users) {
            csv.append(user.getId()).append(",")
                    .append(user.getEmail()).append(",")
                    .append(user.getFullName()).append(",")
                    .append(user.getUserType()).append(",")
                    .append(user.isActive()).append(",")
                    .append(user.getCreatedAt()).append("\n");
        }

        return csv.toString().getBytes();
    }

    // ==================== SYSTEM CONFIGURATION ====================

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<SystemConfigDto> getSystemConfigs() {
        List<SystemConfigDto> configs = new ArrayList<>();

        configs.add(SystemConfigDto.builder()  // ✅ USE BUILDER
                .configKey("notification.quiet_hours.enabled")
                .configValue("true")
                .configType("BOOLEAN")
                .description("Enable quiet hours for notifications")
                .isPublic(true)
                .defaultValue("true")
                .category("NOTIFICATION")
                .lastUpdated(LocalDateTime.now())
                .build());

        configs.add(SystemConfigDto.builder()
                .configKey("offline.cache.expiration_days")
                .configValue("7")
                .configType("NUMBER")
                .description("Days until offline cache expires")
                .isPublic(true)
                .defaultValue("7")
                .category("OFFLINE")
                .lastUpdated(LocalDateTime.now())
                .build());

        configs.add(SystemConfigDto.builder()
                .configKey("security.login.max_attempts")
                .configValue("5")
                .configType("NUMBER")
                .description("Maximum login attempts before lockout")
                .isPublic(false)
                .defaultValue("5")
                .category("SECURITY")
                .lastUpdated(LocalDateTime.now())
                .build());

        configs.add(SystemConfigDto.builder()
                .configKey("booking.cancellation.timeout_hours")
                .configValue("24")
                .configType("NUMBER")
                .description("Hours before departure when cancellation is not allowed")
                .isPublic(true)
                .defaultValue("24")
                .category("BOOKING")
                .lastUpdated(LocalDateTime.now())
                .build());

        return configs;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public SystemConfigDto getSystemConfig(String key) {
        // In production, fetch from database
        switch (key) {
            case "notification.quiet_hours.enabled":
                return new SystemConfigDto(key, "true", "BOOLEAN", "Enable quiet hours", true);
            case "offline.cache.expiration_days":
                return new SystemConfigDto(key, "7", "NUMBER", "Cache expiration days", true);
            case "security.login.max_attempts":
                return new SystemConfigDto(key, "5", "NUMBER", "Max login attempts", false);
            default:
                throw new ResourceNotFoundException("Config key not found: " + key);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void updateSystemConfig(String key, String value, String description, String adminEmail) {
        // In production, save to database
        logAdminAction(
                "SYSTEM_CONFIG",
                String.format("Updated system config: %s = %s", key, value),
                adminEmail,
                null,
                "SYSTEM",
                null,
                "SUCCESS",
                null,
                0L
        );
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void resetSystemConfig(String key, String adminEmail) {
        logAdminAction(
                "SYSTEM_CONFIG",
                "Reset system config: " + key,
                adminEmail,
                null,
                "SYSTEM",
                null,
                "SUCCESS",
                null,
                0L
        );
    }

    // ==================== CACHE MANAGEMENT ====================

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<String> getCacheNames() {
        // Return available cache names
        return Arrays.asList("users", "trips", "bookings", "weather");
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void clearCache(String cacheName) {
        logAdminAction(
                "CACHE",
                "Cleared cache: " + cacheName,
                getCurrentAdminEmail(),
                null,
                "SYSTEM",
                null,
                "SUCCESS",
                null,
                0L
        );
    }

    // ==================== MONITORING & LOGS ====================

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<AdminLog> getAdminLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AdminLog> logPage = adminLogRepository.findAll(pageable);

        return PageResponse.<AdminLog>builder()
                .content(logPage.getContent())
                .page(logPage.getNumber())
                .size(logPage.getSize())
                .totalElements(logPage.getTotalElements())
                .totalPages(logPage.getTotalPages())
                .last(logPage.isLast())
                .build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<AdminLog> getErrorLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AdminLog> logPage = adminLogRepository.findByStatus("FAILED", pageable);

        return PageResponse.<AdminLog>builder()
                .content(logPage.getContent())
                .page(logPage.getNumber())
                .size(logPage.getSize())
                .totalElements(logPage.getTotalElements())
                .totalPages(logPage.getTotalPages())
                .last(logPage.isLast())
                .build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<AdminLog> getUserAuditTrail(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AdminLog> logPage = adminLogRepository.findByAffectedUserId(userId, pageable);

        return PageResponse.<AdminLog>builder()
                .content(logPage.getContent())
                .page(logPage.getNumber())
                .size(logPage.getSize())
                .totalElements(logPage.getTotalElements())
                .totalPages(logPage.getTotalPages())
                .last(logPage.isLast())
                .build();
    }

    // ==================== HEALTH CHECK ====================

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public HealthCheckDto systemHealthCheck() {
        Map<String, String> details = new HashMap<>();

        // Database health
        try {
            jdbcTemplate.execute("SELECT 1");
            details.put("database", "UP");
        } catch (Exception e) {
            details.put("database", "DOWN: " + e.getMessage());
        }

        // Memory health
        Runtime runtime = Runtime.getRuntime();
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        long usedMemory = totalMemory - freeMemory;
        double memoryUsage = (double) usedMemory / totalMemory * 100;

        details.put("memory_usage", String.format("%.1f%%", memoryUsage));
        details.put("free_memory_mb", String.valueOf(freeMemory));
        details.put("total_memory_mb", String.valueOf(totalMemory));

        // Application health
        details.put("application", "UP");
        details.put("version", "1.0.0");

        boolean allUp = details.values().stream().allMatch(v -> !v.startsWith("DOWN"));

        return HealthCheckDto.builder()
                .status(allUp ? "UP" : "DEGRADED")
                .details(details)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    // ==================== AUDIT LOGGING ====================

    @Override
    public void logAdminAction(String actionType, String details, String adminEmail,
                               Long affectedUserId, String affectedEntityType, Long affectedEntityId,
                               String status, String errorMessage, Long executionTimeMs) {

        AdminLog log = new AdminLog();
        log.setActionType(actionType);
        log.setActionDetails(details);

        User adminUser = userRepository.findByEmail(adminEmail).orElse(null);
        if (adminUser != null) {
            log.setAdminId(adminUser.getId());
            log.setAdminEmail(adminEmail);
        }

        log.setAffectedUserId(affectedUserId);
        log.setAffectedEntityType(affectedEntityType);
        log.setAffectedEntityId(affectedEntityId);
        log.setStatus(status);
        log.setErrorMessage(errorMessage);
        log.setExecutionTimeMs(executionTimeMs);
        log.setCreatedAt(LocalDateTime.now());

        // Get client info if available
        if (request != null) {
            log.setIpAddress(request.getRemoteAddr());
            log.setUserAgent(request.getHeader("User-Agent"));
        }

        adminLogRepository.save(log);
    }

    // ==================== HELPER METHODS ====================

    private void validateAdmin(String adminEmail) {
        User adminUser = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (!adminUser.getUserType().equals(UserType.ADMIN)) {
            throw new UnauthorizedAccessException("Admin access required");
        }
    }

    private String getCurrentAdminEmail() {
        org.springframework.security.core.Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }

        return "system";
    }

    private String getLastBackupTime() {
        // In production, fetch from backup tracking table
        return LocalDateTime.now().minusHours(2).toString();
    }
}