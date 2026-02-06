package com.travelplanner.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchPreferenceDto {
    private Long id;
    private String preferenceType;
    private String preferenceKey;
    private String preferenceValue;
    private Integer priority;
}
