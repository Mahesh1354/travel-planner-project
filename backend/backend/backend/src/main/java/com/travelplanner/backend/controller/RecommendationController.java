package com.travelplanner.backend.controller;


import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.service.JwtService;
import com.travelplanner.backend.service.RecommendationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private JwtService jwtService;

    // Helper method to extract user email from JWT token
    private String getCurrentUserEmail(String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Remove "Bearer "
        return jwtService.getEmailFromToken(token);
    }

    // General recommendations
    @PostMapping("/search")
    public ResponseEntity<ApiResponse> getRecommendations(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody RecommendationRequest request) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        List<RecommendationResponse> recommendations =
                recommendationService.getRecommendations(request, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Recommendations retrieved successfully", recommendations));
    }

    // Personalized recommendations based on user preferences
    @GetMapping("/personalized/{location}")
    public ResponseEntity<ApiResponse> getPersonalizedRecommendations(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String location) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        List<RecommendationResponse> recommendations =
                recommendationService.getPersonalizedRecommendations(location, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Personalized recommendations retrieved successfully", recommendations));
    }

    // Save a recommendation (user can save their own discoveries)
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> saveRecommendation(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody RecommendationResponse recommendation) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        RecommendationResponse saved = recommendationService.saveRecommendation(recommendation, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Recommendation saved successfully", saved));
    }

    // Get user's saved recommendations
    @GetMapping("/saved")
    public ResponseEntity<ApiResponse> getUserSavedRecommendations(
            @RequestHeader("Authorization") String authorizationHeader) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        List<RecommendationResponse> recommendations =
                recommendationService.getUserSavedRecommendations(userEmail);

        return ResponseEntity.ok(ApiResponse.success("Saved recommendations retrieved successfully", recommendations));
    }

    // Travel tips
    @GetMapping("/tips/{country}")
    public ResponseEntity<ApiResponse> getTravelTips(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String country,
            @RequestParam(required = false) String city) {

        List<TravelTipResponse> tips = recommendationService.getTravelTips(country, city);

        return ResponseEntity.ok(ApiResponse.success("Travel tips retrieved successfully", tips));
    }

    // Safety alerts
    @GetMapping("/alerts/{country}")
    public ResponseEntity<ApiResponse> getSafetyAlerts(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String country) {

        List<TravelTipResponse> alerts = recommendationService.getSafetyAlerts(country);

        return ResponseEntity.ok(ApiResponse.success("Safety alerts retrieved successfully", alerts));
    }

    // Visa requirements
    @GetMapping("/visa/{country}")
    public ResponseEntity<ApiResponse> getVisaRequirements(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String country) {

        TravelTipResponse visaInfo = recommendationService.getVisaRequirements(country);

        return ResponseEntity.ok(ApiResponse.success("Visa requirements retrieved successfully", visaInfo));
    }

    // Cultural tips
    @GetMapping("/culture/{country}")
    public ResponseEntity<ApiResponse> getCulturalTips(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String country) {

        List<TravelTipResponse> culturalTips = recommendationService.getCulturalTips(country);

        return ResponseEntity.ok(ApiResponse.success("Cultural tips retrieved successfully", culturalTips));
    }

    // User preference management
    @PostMapping("/preferences")
    public ResponseEntity<ApiResponse> saveUserPreference(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody UserPreferenceRequest request) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        UserPreferenceResponse response = recommendationService.saveUserPreference(request, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Preference saved successfully", response));
    }

    @GetMapping("/preferences")
    public ResponseEntity<ApiResponse> getUserPreferences(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(required = false) String preferenceType) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        List<UserPreferenceResponse> preferences =
                recommendationService.getUserPreferences(userEmail, preferenceType);

        return ResponseEntity.ok(ApiResponse.success("Preferences retrieved successfully", preferences));
    }

    @DeleteMapping("/preferences/{preferenceId}")
    public ResponseEntity<ApiResponse> deleteUserPreference(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long preferenceId) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        recommendationService.deleteUserPreference(preferenceId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Preference deleted successfully", null));
    }

    @PutMapping("/preferences/{preferenceId}")
    public ResponseEntity<ApiResponse> updateUserPreference(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long preferenceId,
            @Valid @RequestBody UserPreferenceRequest request) {

        String userEmail = getCurrentUserEmail(authorizationHeader);
        recommendationService.updateUserPreference(preferenceId, request, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Preference updated successfully", null));
    }

    // Development endpoints (might want to secure these in production)
    @PostMapping("/generate-mock-data")
    public ResponseEntity<ApiResponse> generateMockData(
            @RequestHeader("Authorization") String authorizationHeader) {

        recommendationService.generateMockRecommendations();
        recommendationService.generateMockTravelTips();

        return ResponseEntity.ok(ApiResponse.success("Mock data generated successfully", null));
    }
}
