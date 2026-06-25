package com.travelplanner.backend.service.external.impl;

import com.travelplanner.backend.config.external.ExternalApiProperties;
import com.travelplanner.backend.dto.external.*;
import com.travelplanner.backend.service.external.FlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AmadeusFlightService implements FlightService {

    private static final Logger logger = LoggerFactory.getLogger(AmadeusFlightService.class);
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Autowired
    private ExternalApiProperties externalApiProperties;

    @Autowired
    @Qualifier("flightRestTemplate")
    private RestTemplate restTemplate;

    private String accessToken;
    private LocalDateTime tokenExpiry;

    @Override
    public List<FlightResponse> searchFlights(FlightSearchRequest request) {
        try {
            ensureValidToken();

            String url = UriComponentsBuilder.fromPath("/v2/shopping/flight-offers")
                    .queryParam("originLocationCode", request.getOriginCode())
                    .queryParam("destinationLocationCode", request.getDestinationCode())
                    .queryParam("departureDate", request.getDepartureDate())
                    .queryParam("returnDate", request.getReturnDate())
                    .queryParam("adults", request.getAdults())
                    .queryParam("children", request.getChildren())
                    .queryParam("travelClass", request.getCabinClass())
                    .queryParam("currencyCode", request.getCurrency())
                    .queryParam("max", request.getMaxResults() > 0 ? request.getMaxResults() : 20)
                    .build()
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<AmadeusFlightResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    AmadeusFlightResponse.class
            );

            return mapToFlightResponses(response.getBody());

        } catch (Exception e) {
            logger.error("Error searching flights: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search flights", e);
        }
    }

    @Override
    public CompletableFuture<List<FlightResponse>> searchFlightsAsync(FlightSearchRequest request) {
        return CompletableFuture.supplyAsync(() -> searchFlights(request));
    }

    @Override
    public BookingResponse bookFlight(BookingRequest request) {
        // To be implemented
        throw new UnsupportedOperationException("Flight booking not yet implemented");
    }

    @Override
    public BookingResponse cancelBooking(String bookingReference) {
        // To be implemented
        throw new UnsupportedOperationException("Flight cancellation not yet implemented");
    }

    @Override
    public BookingResponse getBookingDetails(String bookingReference) {
        // To be implemented
        throw new UnsupportedOperationException("Get booking details not yet implemented");
    }

    private void ensureValidToken() {
        if (accessToken == null || tokenExpiry == null || LocalDateTime.now().isAfter(tokenExpiry)) {
            authenticate();
        }
    }

    private void authenticate() {
        String authUrl = externalApiProperties.getFlight().getBaseUrl() + "/v1/security/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=client_credentials" +
                "&client_id=" + externalApiProperties.getFlight().getApiKey() +
                "&client_secret=" + externalApiProperties.getFlight().getApiSecret();

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<AmadeusAuthResponse> response = restTemplate.postForEntity(
                    authUrl,
                    entity,
                    AmadeusAuthResponse.class
            );

            AmadeusAuthResponse authResponse = response.getBody();
            if (authResponse != null) {
                this.accessToken = authResponse.getAccessToken();
                this.tokenExpiry = LocalDateTime.now().plusSeconds(authResponse.getExpiresIn());
                logger.info("Successfully authenticated with Amadeus API");
            }
        } catch (Exception e) {
            logger.error("Failed to authenticate with Amadeus API: {}", e.getMessage());
            throw new RuntimeException("Amadeus authentication failed", e);
        }
    }

    private List<FlightResponse> mapToFlightResponses(AmadeusFlightResponse amadeusResponse) {
        List<FlightResponse> flights = new ArrayList<>();

        if (amadeusResponse == null || amadeusResponse.getData() == null) {
            return flights;
        }

        for (AmadeusFlightResponse.FlightOffer offer : amadeusResponse.getData()) {
            try {
                if (offer.getItineraries() == null || offer.getItineraries().isEmpty()) {
                    continue;
                }

                AmadeusFlightResponse.FlightOffer.Itinerary itinerary = offer.getItineraries().get(0);

                if (itinerary.getSegments() == null || itinerary.getSegments().isEmpty()) {
                    continue;
                }

                AmadeusFlightResponse.FlightOffer.Segment segment = itinerary.getSegments().get(0);

                FlightResponse flight = FlightResponse.builder()
                        .provider("amadeus")
                        .flightId(offer.getId())
                        .airline(segment.getCarrierCode())
                        .flightNumber(segment.getNumber())
                        .originCode(segment.getDeparture().getIataCode())
                        .destinationCode(segment.getArrival().getIataCode())
                        .departureTime(parseDateTime(segment.getDeparture().getAt()))
                        .arrivalTime(parseDateTime(segment.getArrival().getAt()))
                        .durationMinutes(parseDuration(segment.getDuration()))
                        .price(new BigDecimal(offer.getPrice().getTotal()))
                        .currency(offer.getPrice().getCurrency())
                        .availableSeats(9)
                        .cabinClass("ECONOMY")
                        .build();

                flights.add(flight);

            } catch (Exception e) {
                logger.warn("Failed to map flight offer {}: {}", offer.getId(), e.getMessage());
            }
        }

        logger.info("Mapped {} flights from Amadeus response", flights.size());
        return flights;
    }

    private LocalDateTime parseDateTime(String dateTime) {
        try {
            if (dateTime == null) return LocalDateTime.now();
            // Remove 'Z' and parse
            return LocalDateTime.parse(dateTime.replace("Z", ""));
        } catch (Exception e) {
            logger.warn("Failed to parse date time: {}", dateTime);
            return LocalDateTime.now();
        }
    }

    private int parseDuration(String duration) {
        if (duration == null || duration.isEmpty()) {
            return 120;
        }

        try {
            String timePart = duration.replace("PT", "");
            int hours = 0;
            int minutes = 0;

            if (timePart.contains("H")) {
                String[] parts = timePart.split("H");
                hours = Integer.parseInt(parts[0]);
                if (parts.length > 1 && parts[1].contains("M")) {
                    minutes = Integer.parseInt(parts[1].replace("M", ""));
                }
            } else if (timePart.contains("M")) {
                minutes = Integer.parseInt(timePart.replace("M", ""));
            }

            return hours * 60 + minutes;
        } catch (Exception e) {
            logger.warn("Failed to parse duration: {}", duration);
            return 120;
        }
    }
}