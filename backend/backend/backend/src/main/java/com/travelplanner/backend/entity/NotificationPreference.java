package com.travelplanner.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_preferences",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "notification_type"})
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_type", nullable = false)
    private String notificationType; // FLIGHT_UPDATE, WEATHER_ALERT, GROUP_ACTIVITY, SYSTEM

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "in_app")
    private boolean inApp = true;

    @Column(name = "email")
    private boolean email = true;

    @Column(name = "sms")
    private boolean sms = false;

    @Column(name = "push")
    private boolean push = true;

    @Column(name = "quiet_hours_start")
    private Integer quietHoursStart; // 0-23, e.g., 22 for 10 PM

    @Column(name = "quiet_hours_end")
    private Integer quietHoursEnd; // 0-23, e.g., 7 for 7 AM

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Pre-persist and pre-update methods
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
