package com.travelplanner.backend.service;
import com.travelplanner.backend.dto.SearchResult;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class MockApiService {

    private final Random random = new Random();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public List<SearchResult> searchFlights(String origin, String destination,
                                            String startDate, String cabinClass) {
        List<SearchResult> flights = new ArrayList<>();

        // Generate 5-10 mock flights
        int count = random.nextInt(6) + 5;

        for (int i = 0; i < count; i++) {
            SearchResult flight = new SearchResult();
            flight.setId("FLIGHT-" + UUID.randomUUID().toString().substring(0, 8));
            flight.setSearchType("FLIGHT");
            flight.setProvider("MockAir");

            // Set airline based on random selection
            String[] airlines = {"MockAir", "SkyJet", "Oceanic", "Continental"};
            flight.setAirline(airlines[random.nextInt(airlines.length)]);

            flight.setFlightNumber(generateFlightNumber());
            flight.setOrigin(origin);
            flight.setDestination(destination);

            // Generate random departure and arrival times
            LocalDateTime baseDate = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime departure = baseDate.plusHours(random.nextInt(24));
            LocalDateTime arrival = departure.plusHours(random.nextInt(12) + 2);

            flight.setDepartureTime(departure);
            flight.setArrivalTime(arrival);
            flight.setDuration(calculateDuration(departure, arrival));

            // Set cabin class
            flight.setDetails(new HashMap<>());
            flight.getDetails().put("cabinClass", cabinClass != null ? cabinClass : "ECONOMY");

            // Generate price based on cabin class
            double basePrice = 200.0;
            if ("BUSINESS".equals(cabinClass)) {
                basePrice = 800.0;
            } else if ("FIRST".equals(cabinClass)) {
                basePrice = 1500.0;
            }

            // Add some variation
            double priceVariation = random.nextDouble() * 0.3; // ±30%
            flight.setPrice(basePrice * (1 + priceVariation));

            flight.setCurrency("USD");
            flight.setStops(random.nextInt(3));
            flight.setRating(4.0 + random.nextDouble());
            flight.setReviewCount(random.nextInt(1000) + 100);

            // Set additional details
            Map<String, Object> details = new HashMap<>();
            details.put("aircraft", "Boeing 737");
            details.put("baggageAllowance", "23kg checked, 7kg carry-on");
            details.put("mealIncluded", true);
            details.put("entertainment", "Personal TV");
            flight.setDetails(details);

            flight.setName(flight.getAirline() + " Flight " + flight.getFlightNumber());
            flight.setDescription("Flight from " + origin + " to " + destination);
            flight.setLocation(origin + " → " + destination);

            flights.add(flight);
        }

        return flights;
    }

    public List<SearchResult> searchHotels(String location, String startDate,
                                           String endDate, Integer minRating) {
        List<SearchResult> hotels = new ArrayList<>();

        // Generate 5-10 mock hotels
        int count = random.nextInt(6) + 5;
        String[] hotelChains = {"Grand Plaza", "Ocean View", "City Center", "Mountain Lodge", "Riverside Inn"};
        String[] roomTypes = {"Standard", "Deluxe", "Suite", "Executive"};
        String[] amenitiesList = {"Pool, Spa, Gym, Restaurant", "Free WiFi, Breakfast, Parking",
                "Bar, Conference Room, Shuttle"};

        for (int i = 0; i < count; i++) {
            SearchResult hotel = new SearchResult();
            hotel.setId("HOTEL-" + UUID.randomUUID().toString().substring(0, 8));
            hotel.setSearchType("HOTEL");
            hotel.setProvider("MockHotels");

            // Random hotel name
            int stars = random.nextInt(3) + 3; // 3-5 stars
            if (minRating != null && stars < minRating) {
                stars = minRating;
            }

            hotel.setName(hotelChains[random.nextInt(hotelChains.length)] + " Hotel");
            hotel.setDescription(stars + "-star hotel in " + location);
            hotel.setLocation(location);
            hotel.setStarRating(stars);

            // Generate price
            double basePrice = 80.0 * stars;
            double priceVariation = random.nextDouble() * 0.4; // ±40%
            hotel.setPrice(basePrice * (1 + priceVariation));
            hotel.setCurrency("USD");

            hotel.setRating(3.5 + (stars * 0.3) + random.nextDouble() * 0.5);
            hotel.setReviewCount(random.nextInt(2000) + 200);

            // Set hotel specific details
            hotel.setHotelChain(hotelChains[random.nextInt(hotelChains.length)]);
            hotel.setRoomType(roomTypes[random.nextInt(roomTypes.length)]);
            hotel.setAmenities(amenitiesList[random.nextInt(amenitiesList.length)]);

            // Set availability dates
            LocalDateTime baseStart = LocalDateTime.parse(startDate + "T14:00:00");
            LocalDateTime baseEnd = endDate != null ?
                    LocalDateTime.parse(endDate + "T11:00:00") :
                    baseStart.plusDays(3);

            hotel.setAvailableFrom(baseStart);
            hotel.setAvailableTo(baseEnd);

            // Set additional details
            Map<String, Object> details = new HashMap<>();
            details.put("checkInTime", "14:00");
            details.put("checkOutTime", "11:00");
            details.put("freeCancellation", random.nextBoolean());
            details.put("breakfastIncluded", random.nextBoolean());
            details.put("parkingAvailable", random.nextBoolean());
            details.put("petFriendly", random.nextBoolean());
            hotel.setDetails(details);

            hotels.add(hotel);
        }

        return hotels;
    }

    public List<SearchResult> searchActivities(String location, String startDate,
                                               String activityType) {
        List<SearchResult> activities = new ArrayList<>();

        // Generate 5-10 mock activities
        int count = random.nextInt(6) + 5;
        String[] activityTypes = {"adventure", "cultural", "food", "nature", "sightseeing"};
        String[] activityNames = {"City Tour", "Food Tasting", "Hiking Adventure",
                "Museum Visit", "Boat Cruise", "Cooking Class"};
        String[] durations = {"2 hours", "4 hours", "Full day", "Half day"};

        for (int i = 0; i < count; i++) {
            SearchResult activity = new SearchResult();
            activity.setId("ACTIVITY-" + UUID.randomUUID().toString().substring(0, 8));
            activity.setSearchType("ACTIVITY");
            activity.setProvider("MockActivities");

            // Set activity type
            String type = activityType != null ? activityType :
                    activityTypes[random.nextInt(activityTypes.length)];
            activity.setActivityType(type);

            // Generate name and description
            activity.setName(activityNames[random.nextInt(activityNames.length)] + " in " + location);
            activity.setDescription(type.substring(0, 1).toUpperCase() + type.substring(1) +
                    " activity in " + location);
            activity.setLocation(location);

            // Generate price
            double basePrice = 50.0 + random.nextDouble() * 100;
            activity.setPrice(basePrice);
            activity.setCurrency("USD");

            activity.setRating(4.0 + random.nextDouble());
            activity.setReviewCount(random.nextInt(500) + 50);

            // Set activity specific details
            activity.setDurationHours(durations[random.nextInt(durations.length)]);
            activity.setGuideLanguage("English");
            activity.setMaxParticipants(random.nextInt(20) + 5);

            // Set availability
            LocalDateTime baseDate = LocalDateTime.parse(startDate + "T09:00:00");
            LocalDateTime startTime = baseDate.plusHours(random.nextInt(8));
            LocalDateTime endTime = startTime.plusHours(2 + random.nextInt(6));

            activity.setAvailableFrom(startTime);
            activity.setAvailableTo(endTime);

            // Set additional details
            Map<String, Object> details = new HashMap<>();
            details.put("difficulty", "Moderate");
            details.put("minAge", 12);
            details.put("equipmentProvided", random.nextBoolean());
            details.put("transportationIncluded", random.nextBoolean());
            details.put("guideIncluded", true);
            activity.setDetails(details);

            activities.add(activity);
        }

        return activities;
    }

    // Helper methods
    private String generateFlightNumber() {
        return String.valueOf(100 + random.nextInt(900)) +
                (char) ('A' + random.nextInt(26));
    }

    private String calculateDuration(LocalDateTime departure, LocalDateTime arrival) {
        long hours = java.time.Duration.between(departure, arrival).toHours();
        long minutes = java.time.Duration.between(departure, arrival).toMinutes() % 60;
        return String.format("%dh %dm", hours, minutes);
    }
}