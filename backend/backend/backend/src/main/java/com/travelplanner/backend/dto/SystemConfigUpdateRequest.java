package com.travelplanner.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigUpdateRequest {

    @NotBlank(message = "Config value is required")
    private String value;

    private String description;

    private boolean resetToDefault = false;
}