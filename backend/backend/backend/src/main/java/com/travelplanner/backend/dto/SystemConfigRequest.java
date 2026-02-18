package com.travelplanner.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigRequest {
    private String configKey;
    private String configValue;
    private String configType; // STRING, NUMBER, BOOLEAN, JSON
    private String description;
    private boolean isPublic = false;
}
