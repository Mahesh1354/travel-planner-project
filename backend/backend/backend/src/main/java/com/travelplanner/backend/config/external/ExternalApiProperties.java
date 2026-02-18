package com.travelplanner.backend.config.external;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "external.api")
public class ExternalApiProperties {

    private FlightApiProperties flight = new FlightApiProperties();
    private WeatherApiProperties weather = new WeatherApiProperties();
    private MapsApiProperties maps = new MapsApiProperties();

    @Data
    public static class FlightApiProperties {
        private String provider = "amadeus";
        private String baseUrl = "${FLIGHT_API_URL:}";
        private String apiKey = "${FLIGHT_API_KEY:}";
        private String apiSecret = "${FLIGHT_API_SECRET:}";
        private int timeoutMs = 10000;
        private int maxRetries = 3;
    }

    @Data
    public static class WeatherApiProperties {
        private String provider = "openweather";
        private String baseUrl = "${WEATHER_API_URL:https://api.openweathermap.org/data/2.5}";
        private String apiKey = "${WEATHER_API_KEY:}";
        private int timeoutMs = 5000;
        private String units = "metric";
    }

    @Data
    public static class MapsApiProperties {
        private String provider = "google";
        private String baseUrl = "${MAPS_API_URL:https://maps.googleapis.com/maps/api}";
        private String apiKey = "${MAPS_API_KEY:}";
        private int timeoutMs = 5000;
    }
}