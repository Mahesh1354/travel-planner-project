package com.travelplanner.backend.service.impl;

import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.entity.*;
import com.travelplanner.backend.exception.ResourceNotFoundException;
import com.travelplanner.backend.repository.*;
import com.travelplanner.backend.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private TravelTipRepository travelTipRepository;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Override
    public List<RecommendationResponse> getRecommendations(RecommendationRequest request, String userEmail) {
        List<Recommendation> recommendations;

        // Base query by location
        if (request.getLocation() == null || request.getLocation().isEmpty()) {
            throw new IllegalArgumentException("Location is required for recommendations");
        }

        // Apply filters
        if (request.getRecommendationType() != null && request.getCategory() != null) {
            recommendations = recommendationRepository.findByLocationContainingIgnoreCaseAndCategory(
                    request.getLocation(), request.getCategory());
        } else if (request.getRecommendationType() != null) {
            recommendations = recommendationRepository.findByLocationContainingIgnoreCaseAndRecommendationType(
                    request.getLocation(), request.getRecommendationType());
        } else {
            recommendations = recommendationRepository.findByLocationContainingIgnoreCase(request.getLocation());
        }

        // Apply additional filters
        recommendations = recommendations.stream()
                .filter(r -> {
                    if (request.getMaxPriceLevel() != null && r.getPriceLevel() != null) {
                        return r.getPriceLevel() <= request.getMaxPriceLevel();
                    }
                    return true;
                })
                .filter(r -> {
                    if (request.getMinRating() != null && r.getRating() != null) {
                        return r.getRating() >= request.getMinRating();
                    }
                    return true;
                })
                .collect(Collectors.toList());

        // Sort results
        recommendations.sort((a, b) -> {
            int comparison = 0;
            switch (request.getSortBy()) {
                case "rating":
                    comparison = Double.compare(b.getRating() != null ? b.getRating() : 0,
                            a.getRating() != null ? a.getRating() : 0);
                    break;
                case "popularity":
                    comparison = Boolean.compare(b.isPopular(), a.isPopular());
                    break;
                case "price":
                    comparison = Integer.compare(a.getPriceLevel() != null ? a.getPriceLevel() : 4,
                            b.getPriceLevel() != null ? b.getPriceLevel() : 4);
                    break;
                default:
                    comparison = 0;
            }
            return "asc".equalsIgnoreCase(request.getSortOrder()) ? -comparison : comparison;
        });

        // Limit results
        if (request.getLimit() != null && request.getLimit() > 0) {
            recommendations = recommendations.stream()
                    .limit(request.getLimit())
                    .collect(Collectors.toList());
        }

        // Convert to response DTOs
        return recommendations.stream()
                .map(RecommendationResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecommendationResponse> getPersonalizedRecommendations(String location, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get user preferences
        List<UserPreference> preferences = userPreferenceRepository.findByUser(user);

        // Get base recommendations
        List<Recommendation> allRecommendations = recommendationRepository
                .findByLocationContainingIgnoreCase(location);

        if (allRecommendations.isEmpty()) {
            return new ArrayList<>();
        }

        // Calculate relevance scores based on user preferences
        Map<Recommendation, Double> recommendationScores = new HashMap<>();

        for (Recommendation rec : allRecommendations) {
            double score = calculateRelevanceScore(rec, preferences);
            recommendationScores.put(rec, score);
        }

        // Sort by relevance score
        List<Recommendation> sortedRecommendations = new ArrayList<>(allRecommendations);
        sortedRecommendations.sort((a, b) ->
                Double.compare(recommendationScores.get(b), recommendationScores.get(a)));

        // Take top 10 personalized recommendations
        sortedRecommendations = sortedRecommendations.stream()
                .limit(10)
                .collect(Collectors.toList());

        // Convert to response DTOs with relevance scores
        return sortedRecommendations.stream()
                .map(rec -> {
                    RecommendationResponse response = new RecommendationResponse(rec);
                    response.setRelevanceScore(recommendationScores.get(rec));
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RecommendationResponse saveRecommendation(RecommendationResponse recommendationDto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Recommendation recommendation = new Recommendation();
        recommendation.setRecommendationType(recommendationDto.getRecommendationType());
        recommendation.setTitle(recommendationDto.getTitle());
        recommendation.setDescription(recommendationDto.getDescription());
        recommendation.setCategory(recommendationDto.getCategory());
        recommendation.setLocation(recommendationDto.getLocation());
        recommendation.setAddress(recommendationDto.getAddress());
        recommendation.setLatitude(recommendationDto.getLatitude());
        recommendation.setLongitude(recommendationDto.getLongitude());
        recommendation.setRating(recommendationDto.getRating());
        recommendation.setPriceLevel(recommendationDto.getPriceLevel());
        recommendation.setOpeningHours(recommendationDto.getOpeningHours());
        recommendation.setWebsite(recommendationDto.getWebsite());
        recommendation.setPhone(recommendationDto.getPhone());
        recommendation.setTags(recommendationDto.getTags());
        recommendation.setPopular(recommendationDto.isPopular());
        recommendation.setSeasonal(recommendationDto.isSeasonal());
        recommendation.setUser(user);
        recommendation.setSource("USER");

        Recommendation savedRecommendation = recommendationRepository.save(recommendation);
        return new RecommendationResponse(savedRecommendation);
    }

    @Override
    @Transactional
    public void deleteRecommendation(Long recommendationId, String userEmail) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found"));

        // Check if user owns the recommendation
        if (recommendation.getUser() == null || !recommendation.getUser().getEmail().equals(userEmail)) {
            throw new IllegalArgumentException("You can only delete your own recommendations");
        }

        recommendationRepository.delete(recommendation);
    }

    @Override
    public List<RecommendationResponse> getUserSavedRecommendations(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // In a real implementation, you would have a separate table for saved recommendations
        // For now, we'll return recommendations where user is set
        List<Recommendation> recommendations = recommendationRepository.findAll().stream()
                .filter(r -> r.getUser() != null && r.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());

        return recommendations.stream()
                .map(RecommendationResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<TravelTipResponse> getTravelTips(String country, String city) {
        List<TravelTip> tips;

        if (city != null && !city.isEmpty()) {
            tips = travelTipRepository.findByCountryAndCity(country, city);
        } else {
            tips = travelTipRepository.findByCountry(country);
        }

        // Sort by priority level (most important first)
        tips.sort((a, b) -> Integer.compare(a.getPriorityLevel(), b.getPriorityLevel()));

        return tips.stream()
                .map(TravelTipResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<TravelTipResponse> getSafetyAlerts(String country) {
        List<TravelTip> alerts = travelTipRepository.findByCountryAndTipType(country, "SAFETY");

        // Filter active alerts
        LocalDateTime now = LocalDateTime.now();
        alerts = alerts.stream()
                .filter(alert -> !alert.isAlert() ||
                        alert.getAlertExpiry() == null ||
                        alert.getAlertExpiry().isAfter(now))
                .collect(Collectors.toList());

        return alerts.stream()
                .map(TravelTipResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public TravelTipResponse getVisaRequirements(String country) {
        List<TravelTip> visaTips = travelTipRepository.findByCountryAndTipType(country, "VISA");

        if (visaTips.isEmpty()) {
            // Return a default response if no visa info found
            TravelTip defaultTip = new TravelTip();
            defaultTip.setTipType("VISA");
            defaultTip.setTitle("Visa Information for " + country);
            defaultTip.setContent("Visa requirements vary. Please check with the embassy of " + country + " for the most current information.");
            defaultTip.setCountry(country);
            defaultTip.setPriorityLevel(1);
            return new TravelTipResponse(defaultTip);
        }

        // Return the most recent visa tip
        visaTips.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return new TravelTipResponse(visaTips.get(0));
    }

    @Override
    public List<TravelTipResponse> getCulturalTips(String country) {
        List<TravelTip> culturalTips = travelTipRepository.findByCountryAndTipType(country, "CULTURE");

        return culturalTips.stream()
                .map(TravelTipResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserPreferenceResponse saveUserPreference(UserPreferenceRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if preference already exists
        UserPreference existing = userPreferenceRepository
                .findByUserAndPreferenceTypeAndPreferenceKey(user,
                        request.getPreferenceType(), request.getPreferenceKey());

        if (existing != null) {
            // Update existing preference
            existing.setPreferenceValue(request.getPreferenceValue());
            existing.setWeight(request.getWeight());
            UserPreference updated = userPreferenceRepository.save(existing);
            return new UserPreferenceResponse(updated);
        }

        // Create new preference
        UserPreference preference = new UserPreference();
        preference.setPreferenceType(request.getPreferenceType());
        preference.setPreferenceKey(request.getPreferenceKey());
        preference.setPreferenceValue(request.getPreferenceValue());
        preference.setWeight(request.getWeight());
        preference.setUser(user);

        UserPreference saved = userPreferenceRepository.save(preference);
        return new UserPreferenceResponse(saved);
    }

    @Override
    public List<UserPreferenceResponse> getUserPreferences(String userEmail, String preferenceType) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<UserPreference> preferences;
        if (preferenceType != null) {
            preferences = userPreferenceRepository.findByUserAndPreferenceType(user, preferenceType);
        } else {
            preferences = userPreferenceRepository.findByUser(user);
        }

        return preferences.stream()
                .map(UserPreferenceResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUserPreference(Long preferenceId, String userEmail) {
        UserPreference preference = userPreferenceRepository.findById(preferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Preference not found"));

        // Check if user owns the preference
        if (!preference.getUser().getEmail().equals(userEmail)) {
            throw new IllegalArgumentException("You can only delete your own preferences");
        }

        userPreferenceRepository.delete(preference);
    }

    @Override
    @Transactional
    public void updateUserPreference(Long preferenceId, UserPreferenceRequest request, String userEmail) {
        UserPreference preference = userPreferenceRepository.findById(preferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Preference not found"));

        // Check if user owns the preference
        if (!preference.getUser().getEmail().equals(userEmail)) {
            throw new IllegalArgumentException("You can only update your own preferences");
        }

        preference.setPreferenceValue(request.getPreferenceValue());
        preference.setWeight(request.getWeight());

        userPreferenceRepository.save(preference);
    }

    @Override
    public void generateMockRecommendations() {
        // This would be called during development to populate the database
        // For now, we'll just create a few sample recommendations

        if (recommendationRepository.count() == 0) {
            // Sample recommendations for Hawaii
            Recommendation rec1 = new Recommendation();
            rec1.setRecommendationType("ACTIVITY");
            rec1.setTitle("Snorkeling at Hanauma Bay");
            rec1.setDescription("Protected marine life conservation area with calm waters, perfect for snorkeling.");
            rec1.setCategory("adventure");
            rec1.setLocation("Hawaii, USA");
            rec1.setAddress("7455 Kalanianaʻole Hwy, Honolulu, HI 96825");
            rec1.setRating(4.8);
            rec1.setPriceLevel(2);
            rec1.setOpeningHours("6:00 AM - 6:00 PM");
            rec1.setWebsite("https://hanaumabaystatepark.com");
            rec1.setTags("snorkeling,beach,family-friendly");
            rec1.setPopular(true);
            rec1.setSource("SYSTEM");

            Recommendation rec2 = new Recommendation();
            rec2.setRecommendationType("RESTAURANT");
            rec2.setTitle("Duke's Waikiki");
            rec2.setDescription("Beachfront restaurant serving fresh seafood with live Hawaiian music.");
            rec2.setCategory("food");
            rec2.setLocation("Hawaii, USA");
            rec2.setAddress("2335 Kalakaua Ave, Honolulu, HI 96815");
            rec2.setRating(4.5);
            rec2.setPriceLevel(3);
            rec2.setOpeningHours("7:00 AM - 12:00 AM");
            rec2.setWebsite("https://www.dukeswaikiki.com");
            rec2.setTags("seafood,live-music,ocean-view");
            rec2.setPopular(true);
            rec2.setSource("SYSTEM");

            recommendationRepository.save(rec1);
            recommendationRepository.save(rec2);
        }
    }

    @Override
    public void generateMockTravelTips() {
        if (travelTipRepository.count() == 0) {
            // Sample travel tips for USA
            TravelTip tip1 = new TravelTip();
            tip1.setTipType("VISA");
            tip1.setTitle("Visa Requirements for USA");
            tip1.setContent("Most visitors need either a B-1 (business) or B-2 (tourism) visa. Visa waiver program available for citizens of 40 countries. ESTA authorization required for VWP travelers.");
            tip1.setCountry("USA");
            tip1.setPriorityLevel(1);
            tip1.setSource("GOVT");
            tip1.setSourceUrl("https://travel.state.gov");

            TravelTip tip2 = new TravelTip();
            tip2.setTipType("SAFETY");
            tip2.setTitle("Beach Safety in Hawaii");
            tip2.setContent("Always swim at lifeguarded beaches. Heed warning flags: red = dangerous, yellow = caution, green = safe. Watch for strong currents and riptides.");
            tip2.setCountry("USA");
            tip2.setCity("Hawaii");
            tip2.setPriorityLevel(2);
            tip2.setSource("SYSTEM");

            travelTipRepository.save(tip1);
            travelTipRepository.save(tip2);
        }
    }

    // Helper method to calculate relevance score based on user preferences
    private double calculateRelevanceScore(Recommendation recommendation, List<UserPreference> preferences) {
        double score = 0.5; // Base score

        if (preferences.isEmpty()) {
            return score;
        }

        for (UserPreference pref : preferences) {
            if ("RECOMMENDATION".equals(pref.getPreferenceType())) {
                switch (pref.getPreferenceKey()) {
                    case "category":
                        if (recommendation.getCategory() != null &&
                                recommendation.getCategory().equalsIgnoreCase(pref.getPreferenceValue())) {
                            score += 0.3 * pref.getWeight();
                        }
                        break;
                    case "price_level":
                        if (recommendation.getPriceLevel() != null) {
                            int prefPrice = Integer.parseInt(pref.getPreferenceValue());
                            int diff = Math.abs(recommendation.getPriceLevel() - prefPrice);
                            score += (1 - diff * 0.2) * pref.getWeight(); // Closer price = higher score
                        }
                        break;
                    case "activity_level":
                        if (recommendation.getTags() != null &&
                                recommendation.getTags().contains(pref.getPreferenceValue().toLowerCase())) {
                            score += 0.2 * pref.getWeight();
                        }
                        break;
                }
            }
        }

        // Add bonus for popular items
        if (recommendation.isPopular()) {
            score += 0.1;
        }

        // Add bonus for high ratings
        if (recommendation.getRating() != null && recommendation.getRating() >= 4.5) {
            score += 0.15;
        }

        // Ensure score is between 0 and 1
        return Math.min(Math.max(score, 0), 1);
    }
}