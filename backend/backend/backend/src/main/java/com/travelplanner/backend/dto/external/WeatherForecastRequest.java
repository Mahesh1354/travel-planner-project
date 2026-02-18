package com.travelplanner.backend.dto.external;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherForecastRequest {
    private String city;
    private Double latitude;
    private Double longitude;
    private Integer days = 7;      // 1-16 days
    private String units = "metric";
    private String lang = "en";
}