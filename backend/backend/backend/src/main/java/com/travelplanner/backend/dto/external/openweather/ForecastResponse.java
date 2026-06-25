package com.travelplanner.backend.dto.external.openweather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ForecastResponse {

    private String cod;
    private Integer message;
    private Integer cnt;
    private List<ForecastItem> list;
    private City city;

    @Data
    public static class ForecastItem {

        private Long dt;
        private Main main;
        private List<Weather> weather;

        // ✅ FIXED: Use dedicated inner classes, not references to CurrentWeatherResponse
        private Clouds clouds;
        private Wind wind;
        private Long visibility;
        private Double pop;
        private Rain rain;
        private Snow snow;
        private Sys sys;

        @JsonProperty("dt_txt")
        private String dtTxt;

        @Data
        public static class Main {
            private Double temp;

            @JsonProperty("feels_like")
            private Double feelsLike;

            @JsonProperty("temp_min")
            private Double tempMin;

            @JsonProperty("temp_max")
            private Double tempMax;

            private Double pressure;
            private Double humidity;

            @JsonProperty("sea_level")
            private Integer seaLevel;

            @JsonProperty("grnd_level")
            private Integer groundLevel;

            @JsonProperty("temp_kf")
            private Double tempKf;
        }

        @Data
        public static class Weather {
            private Integer id;
            private String main;
            private String description;
            private String icon;
        }

        // ✅ ADDED: Dedicated Clouds class
        @Data
        public static class Clouds {
            private Double all;
        }

        // ✅ ADDED: Dedicated Wind class
        @Data
        public static class Wind {
            private Double speed;
            private Integer deg;
            private Double gust;
        }

        // ✅ ADDED: Dedicated Rain class
        @Data
        public static class Rain {
            @JsonProperty("1h")
            private Double oneHour;

            @JsonProperty("3h")
            private Double threeHour;
        }

        // ✅ ADDED: Dedicated Snow class
        @Data
        public static class Snow {
            @JsonProperty("1h")
            private Double oneHour;

            @JsonProperty("3h")
            private Double threeHour;
        }

        @Data
        public static class Sys {
            private String pod;
        }
    }

    @Data
    public static class City {
        private Long id;
        private String name;
        private Coord coord;
        private String country;
        private Long population;
        private Integer timezone;
        private Long sunrise;
        private Long sunset;

        @Data
        public static class Coord {
            private Double lat;
            private Double lon;
        }
    }
}