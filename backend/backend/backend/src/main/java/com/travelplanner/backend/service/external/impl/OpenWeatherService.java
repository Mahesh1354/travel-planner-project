package com.travelplanner.backend.service.external.impl;

import com.travelplanner.backend.config.external.ExternalApiProperties;
import com.travelplanner.backend.dto.external.*;
import com.travelplanner.backend.dto.external.openweather.CurrentWeatherResponse;
import com.travelplanner.backend.dto.external.openweather.ForecastResponse;
import com.travelplanner.backend.service.external.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class OpenWeatherService implements WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(OpenWeatherService.class);

    @Autowired
    private ExternalApiProperties externalApiProperties;

    @Autowired
    @Qualifier("weatherRestTemplate")
    private RestTemplate restTemplate;

    private String getApiKey() {
        return externalApiProperties.getWeather().getApiKey();
    }

    private String getBaseUrl() {
        return externalApiProperties.getWeather().getBaseUrl();
    }

    @Override
    public WeatherResponse getCurrentWeather(WeatherRequest request) {
        try {
            String url = buildCurrentWeatherUrl(request);
            logger.debug("Calling OpenWeather current weather API: {}", url);

            CurrentWeatherResponse response = restTemplate.getForObject(
                    url,
                    CurrentWeatherResponse.class
            );

            return mapToCurrentWeatherResponse(response, request.getUnits());

        } catch (Exception e) {
            logger.error("Error fetching current weather: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch current weather", e);
        }
    }

    @Override
    public WeatherResponse getWeatherForecast(WeatherForecastRequest request) {
        try {
            String url = buildForecastUrl(request);
            logger.debug("Calling OpenWeather forecast API: {}", url);

            ForecastResponse response = restTemplate.getForObject(
                    url,
                    ForecastResponse.class
            );

            return mapToForecastResponse(response, request.getUnits());

        } catch (Exception e) {
            logger.error("Error fetching weather forecast: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch weather forecast", e);
        }
    }

    @Override
    public CompletableFuture<WeatherResponse> getCurrentWeatherAsync(WeatherRequest request) {
        return CompletableFuture.supplyAsync(() -> getCurrentWeather(request));
    }

    @Override
    public CompletableFuture<WeatherResponse> getWeatherForecastAsync(WeatherForecastRequest request) {
        return CompletableFuture.supplyAsync(() -> getWeatherForecast(request));
    }

    @Override
    public List<WeatherResponse> getWeatherForCities(List<String> cities) {
        return cities.stream()
                .map(city -> {
                    WeatherRequest request = WeatherRequest.builder()
                            .city(city)
                            .units("metric")
                            .build();
                    try {
                        return getCurrentWeather(request);
                    } catch (Exception e) {
                        logger.warn("Failed to fetch weather for city: {}", city);
                        return null;
                    }
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<WeatherResponse.Alert> getWeatherAlerts(Double lat, Double lon) {
        logger.info("Weather alerts are not available in OpenWeather free tier");
        return new ArrayList<>();
    }

    private String buildCurrentWeatherUrl(WeatherRequest request) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getBaseUrl() + "/weather")
                .queryParam("appid", getApiKey())
                .queryParam("units", request.getUnits())
                .queryParam("lang", request.getLang());

        if (request.getCity() != null && !request.getCity().isEmpty()) {
            if (request.getCountryCode() != null && !request.getCountryCode().isEmpty()) {
                builder.queryParam("q", request.getCity() + "," + request.getCountryCode());
            } else {
                builder.queryParam("q", request.getCity());
            }
        } else if (request.getLatitude() != null && request.getLongitude() != null) {
            builder.queryParam("lat", request.getLatitude())
                    .queryParam("lon", request.getLongitude());
        } else {
            throw new IllegalArgumentException("Either city or lat/lon must be provided");
        }

        return builder.build().toUriString();
    }

    private String buildForecastUrl(WeatherForecastRequest request) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getBaseUrl() + "/forecast")
                .queryParam("appid", getApiKey())
                .queryParam("units", request.getUnits())
                .queryParam("lang", request.getLang());

        if (request.getDays() != null && request.getDays() > 0) {
            // OpenWeather returns 8 forecasts per day (every 3 hours)
            builder.queryParam("cnt", request.getDays() * 8);
        }

        if (request.getCity() != null && !request.getCity().isEmpty()) {
            builder.queryParam("q", request.getCity());
        } else if (request.getLatitude() != null && request.getLongitude() != null) {
            builder.queryParam("lat", request.getLatitude())
                    .queryParam("lon", request.getLongitude());
        } else {
            throw new IllegalArgumentException("Either city or lat/lon must be provided");
        }

        return builder.build().toUriString();
    }

    private WeatherResponse mapToCurrentWeatherResponse(
            CurrentWeatherResponse response,
            String units) {

        if (response == null) return null;

        WeatherResponse.CurrentWeather current = WeatherResponse.CurrentWeather.builder()
                .temperature(response.getMain() != null ? response.getMain().getTemp() : null)
                .feelsLike(response.getMain() != null ? response.getMain().getFeelsLike() : null)
                .humidity(response.getMain() != null ? response.getMain().getHumidity() : null)
                .pressure(response.getMain() != null ? response.getMain().getPressure() : null)
                .windSpeed(response.getWind() != null ? response.getWind().getSpeed() : null)
                .windDeg(response.getWind() != null ? response.getWind().getDeg() : null)
                .cloudiness(response.getClouds() != null ? response.getClouds().getAll() : null)
                .visibility(response.getVisibility() != null ? response.getVisibility() + "m" : null)
                .condition(response.getWeather() != null && !response.getWeather().isEmpty() ?
                        response.getWeather().get(0).getMain() : null)
                .description(response.getWeather() != null && !response.getWeather().isEmpty() ?
                        response.getWeather().get(0).getDescription() : null)
                .icon(response.getWeather() != null && !response.getWeather().isEmpty() ?
                        response.getWeather().get(0).getIcon() : null)
                .sunrise(response.getSys() != null && response.getSys().getSunrise() != null ?
                        LocalDateTime.ofInstant(Instant.ofEpochSecond(response.getSys().getSunrise()),
                                ZoneId.systemDefault()) : null)
                .sunset(response.getSys() != null && response.getSys().getSunset() != null ?
                        LocalDateTime.ofInstant(Instant.ofEpochSecond(response.getSys().getSunset()),
                                ZoneId.systemDefault()) : null)
                .build();

        return WeatherResponse.builder()
                .provider("openweather")
                .cityName(response.getName())
                .countryCode(response.getSys() != null ? response.getSys().getCountry() : null)
                .latitude(response.getCoord() != null ? response.getCoord().getLat() : null)
                .longitude(response.getCoord() != null ? response.getCoord().getLon() : null)
                .current(current)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private WeatherResponse mapToForecastResponse(
            ForecastResponse response,
            String units) {

        if (response == null || response.getList() == null) return null;

        List<WeatherResponse.Forecast> forecasts = response.getList().stream()
                .map(item -> {
                    Double cloudiness = null;
                    if (item.getClouds() != null) {
                        cloudiness = item.getClouds().getAll();
                    }

                    Double windSpeed = null;
                    Integer windDeg = null;
                    if (item.getWind() != null) {
                        windSpeed = item.getWind().getSpeed();
                        windDeg = item.getWind().getDeg();
                    }

                    Double precipitation = 0.0;
                    if (item.getPop() != null) {
                        precipitation = item.getPop() * 100;
                    }

                    return WeatherResponse.Forecast.builder()
                            .dateTime(item.getDt() != null ?
                                    LocalDateTime.ofInstant(Instant.ofEpochSecond(item.getDt()),
                                            ZoneId.systemDefault()) : null)
                            .temperature(item.getMain() != null ? item.getMain().getTemp() : null)
                            .feelsLike(item.getMain() != null ? item.getMain().getFeelsLike() : null)
                            .humidity(item.getMain() != null ? item.getMain().getHumidity() : null)
                            .pressure(item.getMain() != null ? item.getMain().getPressure() : null)
                            .windSpeed(windSpeed)
                            .windDeg(windDeg)
                            .condition(item.getWeather() != null && !item.getWeather().isEmpty() ?
                                    item.getWeather().get(0).getMain() : null)
                            .description(item.getWeather() != null && !item.getWeather().isEmpty() ?
                                    item.getWeather().get(0).getDescription() : null)
                            .icon(item.getWeather() != null && !item.getWeather().isEmpty() ?
                                    item.getWeather().get(0).getIcon() : null)
                            .precipitation(precipitation)
                            .cloudiness(cloudiness)
                            .build();
                })
                .collect(Collectors.toList());

        return WeatherResponse.builder()
                .provider("openweather")
                .cityName(response.getCity() != null ? response.getCity().getName() : null)
                .countryCode(response.getCity() != null ? response.getCity().getCountry() : null)
                .latitude(response.getCity() != null && response.getCity().getCoord() != null ?
                        response.getCity().getCoord().getLat() : null)
                .longitude(response.getCity() != null && response.getCity().getCoord() != null ?
                        response.getCity().getCoord().getLon() : null)
                .forecast(forecasts)
                .timestamp(LocalDateTime.now())
                .build();
    }
}