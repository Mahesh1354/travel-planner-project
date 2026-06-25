package com.travelplanner.backend.service.external;

import com.travelplanner.backend.dto.external.WeatherRequest;
import com.travelplanner.backend.dto.external.WeatherResponse;
import com.travelplanner.backend.dto.external.WeatherForecastRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface WeatherService {

    /**
     * Get current weather for a city
     * @param request weather request parameters
     * @return current weather data
     */
    WeatherResponse getCurrentWeather(WeatherRequest request);

    /**
     * Get weather forecast for a city
     * @param request forecast request parameters
     * @return weather forecast data
     */
    WeatherResponse getWeatherForecast(WeatherForecastRequest request);

    /**
     * Async get current weather
     */
    CompletableFuture<WeatherResponse> getCurrentWeatherAsync(WeatherRequest request);

    /**
     * Async get weather forecast
     */
    CompletableFuture<WeatherResponse> getWeatherForecastAsync(WeatherForecastRequest request);

    /**
     * Get weather for multiple cities (for trip planning)
     * @param cities list of city names
     * @return list of weather data
     */
    List<WeatherResponse> getWeatherForCities(List<String> cities);

    /**
     * Check for severe weather alerts
     * @param lat latitude
     * @param lon longitude
     * @return list of weather alerts
     */
    List<WeatherResponse.Alert> getWeatherAlerts(Double lat, Double lon);
}