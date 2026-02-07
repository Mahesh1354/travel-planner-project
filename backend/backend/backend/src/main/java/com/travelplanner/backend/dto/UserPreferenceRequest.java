package com.travelplanner.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceRequest {
    private String preferenceType;
    private String preferenceKey;
    private String preferenceValue;
    private Double weight = 1.0;
}
