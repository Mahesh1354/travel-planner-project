package com.travelplanner.backend.dto.external;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherRequest {
    private String city;           // City name (e.g., "London")
    private String countryCode;    // Optional: ISO country code
    private Double latitude;       // Alternative to city name
    private Double longitude;      // Alternative to city name
    private String units = "metric"; // metric, imperial, standard
    private String lang = "en";    // Language for descriptions
}