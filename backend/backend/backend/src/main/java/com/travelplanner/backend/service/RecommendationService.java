package com.travelplanner.backend.service;



import com.travelplanner.backend.dto.*;
import java.util.List;

public interface RecommendationService {

    // Recommendation operations
    List<RecommendationResponse> getRecommendations(RecommendationRequest request, String userEmail);
    List<RecommendationResponse> getPersonalizedRecommendations(String location, String userEmail);
    RecommendationResponse saveRecommendation(RecommendationResponse recommendation, String userEmail);
    void deleteRecommendation(Long recommendationId, String userEmail);
    List<RecommendationResponse> getUserSavedRecommendations(String userEmail);

    // Travel tip operations
    List<TravelTipResponse> getTravelTips(String country, String city);
    List<TravelTipResponse> getSafetyAlerts(String country);
    TravelTipResponse getVisaRequirements(String country);
    List<TravelTipResponse> getCulturalTips(String country);

    // User preference operations
    UserPreferenceResponse saveUserPreference(UserPreferenceRequest request, String userEmail);
    List<UserPreferenceResponse> getUserPreferences(String userEmail, String preferenceType);
    void deleteUserPreference(Long preferenceId, String userEmail);
    void updateUserPreference(Long preferenceId, UserPreferenceRequest request, String userEmail);

    // Mock data generation (for development)
    void generateMockRecommendations();
    void generateMockTravelTips();
}
