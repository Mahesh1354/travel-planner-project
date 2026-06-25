package com.travelplanner.backend.controller;

import com.travelplanner.backend.dto.ApiResponse;
import com.travelplanner.backend.dto.external.WeatherRequest;
import com.travelplanner.backend.dto.external.WeatherResponse;
import com.travelplanner.backend.dto.external.WeatherForecastRequest;
import com.travelplanner.backend.service.external.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/current")
    public ResponseEntity<ApiResponse> getCurrentWeather(@Valid WeatherRequest request) {
        try {
            WeatherResponse response = weatherService.getCurrentWeather(request);
            return ResponseEntity.ok(ApiResponse.success("Current weather retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to retrieve weather: " + e.getMessage()));
        }
    }

    @GetMapping("/forecast")
    public ResponseEntity<ApiResponse> getWeatherForecast(@Valid WeatherForecastRequest request) {
        try {
            WeatherResponse response = weatherService.getWeatherForecast(request);
            return ResponseEntity.ok(ApiResponse.success("Weather forecast retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to retrieve forecast: " + e.getMessage()));
        }
    }

    @PostMapping("/cities")
    public ResponseEntity<ApiResponse> getWeatherForCities(@RequestBody List<String> cities) {
        try {
            List<WeatherResponse> responses = weatherService.getWeatherForCities(cities);
            return ResponseEntity.ok(ApiResponse.success("Weather for cities retrieved successfully", responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to retrieve weather for cities: " + e.getMessage()));
        }
    }
}