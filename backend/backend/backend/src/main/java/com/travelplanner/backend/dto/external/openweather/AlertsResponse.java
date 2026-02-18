package com.travelplanner.backend.dto.external.openweather;

import lombok.Data;
import java.util.List;

// TODO: OpenWeather free tier does not support alerts
// This is for One Call API 3.0 (paid subscription)
@Data
public class AlertsResponse {

    private List<AlertFeature> features;

    @Data
    public static class AlertFeature {
        private String id;
        private String type;
        private Properties properties;

        @Data
        public static class Properties {
            private String event;
            private String headline;
            private String description;
            private String severity;
            private String urgency;
            private String areas;
            private String category;
            private String effective;
            private String expires;
            private String status;
        }
    }
}