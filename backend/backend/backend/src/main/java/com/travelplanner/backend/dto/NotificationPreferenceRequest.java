package com.travelplanner.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferenceRequest {
    private String notificationType; // FLIGHT_UPDATE, WEATHER_ALERT, GROUP_ACTIVITY, SYSTEM
    private boolean enabled = true;
    private boolean inApp = true;
    private boolean email = true;
    private boolean sms = false;
    private boolean push = true;
    private Integer quietHoursStart; // 0-23
    private Integer quietHoursEnd; // 0-23
}
