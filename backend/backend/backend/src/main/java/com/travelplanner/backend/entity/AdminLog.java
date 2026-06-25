package com.travelplanner.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_logs", indexes = {
        @Index(name = "idx_admin_logs_admin_id", columnList = "admin_id"),
        @Index(name = "idx_admin_logs_affected_user", columnList = "affected_user_id"),
        @Index(name = "idx_admin_logs_created_at", columnList = "created_at"),
        @Index(name = "idx_admin_logs_status", columnList = "status"),
        @Index(name = "idx_admin_logs_action_type", columnList = "action_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType; // USER_MANAGEMENT, DATA_CLEANUP, SYSTEM_CONFIG

    @Column(name = "action_details", length = 2000)
    private String actionDetails;

    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "admin_email", length = 100)
    private String adminEmail;

    @Column(name = "affected_user_id")
    private Long affectedUserId;

    @Column(name = "affected_entity_type", length = 50)
    private String affectedEntityType; // USER, TRIP, BOOKING, etc.

    @Column(name = "affected_entity_id")
    private Long affectedEntityId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "status", length = 20)
    private String status = "SUCCESS"; // SUCCESS, FAILED, PENDING

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Pre-persist method
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}