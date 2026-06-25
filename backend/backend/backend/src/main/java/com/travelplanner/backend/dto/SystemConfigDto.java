package com.travelplanner.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigDto {
    private String configKey;
    private String configValue;
    private String configType; // STRING, NUMBER, BOOLEAN, JSON
    private String description;
    private boolean isPublic;
    private String defaultValue;
    private String validationRegex;
    private String[] allowedValues;
    private LocalDateTime lastUpdated;
    private String updatedBy;
    private String category; // NOTIFICATION, SECURITY, BOOKING, etc.


    // ✅ Constructor 2: 5-parameter constructor for your service calls
    public SystemConfigDto(String configKey, String configValue, String configType,
                           String description, boolean isPublic) {
        this.configKey = configKey;
        this.configValue = configValue;
        this.configType = configType;
        this.description = description;
        this.isPublic = isPublic;
        this.defaultValue = configValue;
        this.lastUpdated = LocalDateTime.now();
        this.category = extractCategory(configKey);
    }

    // ✅ Constructor 3: Minimal constructor for quick creation
    public SystemConfigDto(String configKey, String configValue) {
        this.configKey = configKey;
        this.configValue = configValue;
        this.configType = "STRING";
        this.description = "";
        this.isPublic = false;
        this.defaultValue = configValue;
        this.lastUpdated = LocalDateTime.now();
        this.category = extractCategory(configKey);
    }

    private String extractCategory(String key) {
        if (key == null) return "GENERAL";
        if (key.startsWith("notification.")) return "NOTIFICATION";
        if (key.startsWith("offline.")) return "OFFLINE";
        if (key.startsWith("security.")) return "SECURITY";
        if (key.startsWith("booking.")) return "BOOKING";
        return "GENERAL";
    }
}