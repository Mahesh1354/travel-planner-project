package com.travelplanner.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferenceResponse {
    private Long id;
    private String notificationType;
    private boolean enabled;
    private boolean inApp;
    private boolean email;
    private boolean sms;
    private boolean push;
    private Integer quietHoursStart;
    private Integer quietHoursEnd;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponse user;

    // Constructor from NotificationPreference entity
    public NotificationPreferenceResponse(com.travelplanner.backend.entity.NotificationPreference preference) {
        this.id = preference.getId();
        this.notificationType = preference.getNotificationType();
        this.enabled = preference.isEnabled();
        this.inApp = preference.isInApp();
        this.email = preference.isEmail();
        this.sms = preference.isSms();
        this.push = preference.isPush();
        this.quietHoursStart = preference.getQuietHoursStart();
        this.quietHoursEnd = preference.getQuietHoursEnd();
        this.createdAt = preference.getCreatedAt();
        this.updatedAt = preference.getUpdatedAt();
        this.user = new UserResponse(preference.getUser());
    }
}
