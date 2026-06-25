package com.travelplanner.backend.dto.external;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    private String provider;           // "openweather"
    private String cityName;
    private String countryCode;
    private Double latitude;
    private Double longitude;
    private CurrentWeather current;
    private List<Forecast> forecast;
    private List<Alert> alerts;
    private LocalDateTime timestamp;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentWeather {
        private Double temperature;
        private Double feelsLike;
        private Double humidity;
        private Double pressure;
        private Double windSpeed;
        private Integer windDeg;
        private Double cloudiness;
        private String visibility;
        private String condition;
        private String description;
        private String icon;
        private LocalDateTime sunrise;
        private LocalDateTime sunset;
        private Double uvIndex;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Forecast {
        private LocalDateTime dateTime;
        private Double temperature;
        private Double feelsLike;
        private Double humidity;
        private Double pressure;
        private Double windSpeed;
        private Integer windDeg;
        private String condition;
        private String description;
        private String icon;
        private Double precipitation;
        private Double cloudiness;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Alert {
        private String sender;
        private String event;
        private LocalDateTime start;
        private LocalDateTime end;
        private String description;
        private List<String> tags;
        private String severity; // extreme, severe, moderate, minor
    }
}