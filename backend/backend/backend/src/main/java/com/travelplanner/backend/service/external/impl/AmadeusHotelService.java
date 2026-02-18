package com.travelplanner.backend.service.external.impl;

import com.travelplanner.backend.config.external.ExternalApiProperties;
import com.travelplanner.backend.dto.external.*;
import com.travelplanner.backend.service.external.HotelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class AmadeusHotelService implements HotelService {

    private static final Logger logger = LoggerFactory.getLogger(AmadeusHotelService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    @Autowired
    private ExternalApiProperties externalApiProperties;

    @Autowired
    @Qualifier("flightRestTemplate")
    private RestTemplate restTemplate;

    private String accessToken;
    private LocalDateTime tokenExpiry;

    @Override
    public List<HotelResponse> searchHotels(HotelSearchRequest request) {
        try {
            ensureValidToken();

            // Step 1: Search for hotels by city
            String searchUrl = UriComponentsBuilder.fromPath("/v1/reference-data/locations/hotels/by-city")
                    .queryParam("cityCode", request.getCityCode())
                    .queryParam("radius", 10)
                    .queryParam("radiusUnit", "KM")
                    .queryParam("hotelName", request.getHotelName())
                    .queryParam("amenities", request.getAmenities())
                    .queryParam("ratings", request.getMinRating())
                    .queryParam("limit", request.getMaxResults() > 0 ? request.getMaxResults() : 20)
                    .build()
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<AmadeusHotelResponse> searchResponse = restTemplate.exchange(
                    searchUrl,
                    HttpMethod.GET,
                    entity,
                    AmadeusHotelResponse.class
            );

            // Step 2: Get offers for each hotel if check-in/out dates provided
            List<HotelResponse> hotels = new ArrayList<>();

            if (searchResponse.getBody() != null && searchResponse.getBody().getData() != null) {
                for (AmadeusHotelResponse.HotelData hotelData : searchResponse.getBody().getData()) {
                    HotelResponse hotel = mapToHotelResponse(hotelData);

                    // If dates provided, fetch offers
                    if (request.getCheckInDate() != null && request.getCheckOutDate() != null) {
                        List<HotelResponse.RoomOffer> offers = checkAvailability(
                                hotel.getHotelId(),
                                request.getCheckInDate(),
                                request.getCheckOutDate()
                        );

                        if (!offers.isEmpty()) {
                            HotelResponse.RoomOffer bestOffer = offers.get(0);
                            hotel.setRoomType(bestOffer.getRoomType());
                            hotel.setPricePerNight(bestOffer.getPricePerNight());
                            hotel.setTotalPrice(bestOffer.getTotalPrice());
                            hotel.setCurrency(bestOffer.getCurrency());
                            hotel.setAvailableRooms(bestOffer.getAvailableRooms());
                            hotel.setCancellationPolicy(bestOffer.getCancellationPolicy());
                            hotel.setCheckInTime(request.getCheckInDate().toString());
                            hotel.setCheckOutTime(request.getCheckOutDate().toString());
                        }
                    }

                    hotels.add(hotel);

                    // Apply filters
                    if (request.getMaxPrice() != null) {
                        hotels = hotels.stream()
                                .filter(h -> h.getTotalPrice() != null &&
                                        h.getTotalPrice().compareTo(BigDecimal.valueOf(request.getMaxPrice())) <= 0)
                                .collect(Collectors.toList());
                    }
                }
            }

            logger.info("Found {} hotels in {}", hotels.size(), request.getCityCode());
            return hotels;

        } catch (Exception e) {
            logger.error("Error searching hotels: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search hotels: " + e.getMessage(), e);
        }
    }

    @Override
    public CompletableFuture<List<HotelResponse>> searchHotelsAsync(HotelSearchRequest request) {
        return CompletableFuture.supplyAsync(() -> searchHotels(request));
    }

    @Override
    public HotelResponse getHotelDetails(String hotelId) {
        try {
            ensureValidToken();

            String url = UriComponentsBuilder.fromPath("/v2/shopping/hotel-offers")
                    .queryParam("hotelIds", hotelId)
                    .build()
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<AmadeusHotelOffersResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    AmadeusHotelOffersResponse.class
            );

            if (response.getBody() != null && response.getBody().getData() != null
                    && !response.getBody().getData().isEmpty()) {
                AmadeusHotelOffersResponse.HotelOffer offer = response.getBody().getData().get(0);
                return mapOfferToHotelResponse(offer);
            }

            return null;

        } catch (Exception e) {
            logger.error("Error getting hotel details: {}", e.getMessage());
            throw new RuntimeException("Failed to get hotel details", e);
        }
    }

    @Override
    public List<HotelResponse.RoomOffer> checkAvailability(String hotelId,
                                                           LocalDate checkInDate,
                                                           LocalDate checkOutDate) {
        try {
            ensureValidToken();

            String url = UriComponentsBuilder.fromPath("/v2/shopping/hotel-offers")
                    .queryParam("hotelIds", hotelId)
                    .queryParam("checkInDate", checkInDate.format(DATE_FORMATTER))
                    .queryParam("checkOutDate", checkOutDate.format(DATE_FORMATTER))
                    .queryParam("adults", 2) // Default, can be made configurable
                    .queryParam("roomQuantity", 1)
                    .queryParam("paymentPolicy", "NONE")
                    .queryParam("bestRateOnly", true)
                    .build()
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<AmadeusHotelOffersResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    AmadeusHotelOffersResponse.class
            );

            return mapToRoomOffers(response.getBody());

        } catch (Exception e) {
            logger.error("Error checking hotel availability: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public HotelBookingResponse bookHotel(HotelBookingRequest request) {
        // To be implemented - requires Amadeus Booking API
        throw new UnsupportedOperationException("Hotel booking not yet implemented");
    }

    @Override
    public HotelBookingResponse cancelBooking(String bookingReference) {
        // To be implemented - requires Amadeus Booking API
        throw new UnsupportedOperationException("Hotel cancellation not yet implemented");
    }

    @Override
    public HotelBookingResponse getBookingDetails(String bookingReference) {
        // To be implemented - requires Amadeus Booking API
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
                logger.info("Successfully authenticated with Amadeus API for hotel service");
            }
        } catch (Exception e) {
            logger.error("Failed to authenticate with Amadeus API: {}", e.getMessage());
            throw new RuntimeException("Amadeus authentication failed", e);
        }
    }

    private HotelResponse mapToHotelResponse(AmadeusHotelResponse.HotelData hotelData) {
        return HotelResponse.builder()
                .provider("amadeus")
                .hotelId(hotelData.getHotelId())
                .hotelName(hotelData.getName())
                .hotelChain(hotelData.getChainCode())
                .description(hotelData.getDescription() != null ?
                        hotelData.getDescription().getText() : null)
                .address(hotelData.getAddress() != null ?
                        hotelData.getAddress().getLines() : null)
                .city(hotelData.getAddress() != null ?
                        hotelData.getAddress().getCityName() : null)
                .country(hotelData.getAddress() != null ?
                        hotelData.getAddress().getCountryCode() : null)
                .postalCode(hotelData.getAddress() != null ?
                        hotelData.getAddress().getPostalCode() : null)
                .latitude(hotelData.getGeoCode() != null ?
                        hotelData.getGeoCode().getLatitude() : null)
                .longitude(hotelData.getGeoCode() != null ?
                        hotelData.getGeoCode().getLongitude() : null)
                .starRating(hotelData.getStarRating())
                .userRating(hotelData.getRating())
                .reviewCount(hotelData.getReviewCount())
                .amenities(extractAmenities(hotelData.getAmenities()))
                .images(extractImages(hotelData.getMedia()))
                .build();
    }

    private HotelResponse mapOfferToHotelResponse(AmadeusHotelOffersResponse.HotelOffer offer) {
        return HotelResponse.builder()
                .provider("amadeus")
                .hotelId(offer.getHotelId())
                .hotelName(offer.getHotel().getName())
                .build();
    }

    private List<HotelResponse.RoomOffer> mapToRoomOffers(AmadeusHotelOffersResponse response) {
        List<HotelResponse.RoomOffer> offers = new ArrayList<>();

        if (response == null || response.getData() == null) {
            return offers;
        }

        for (AmadeusHotelOffersResponse.HotelOffer hotelOffer : response.getData()) {
            if (hotelOffer.getOffers() != null) {
                for (AmadeusHotelOffersResponse.HotelOffer.Offer offer : hotelOffer.getOffers()) {
                    HotelResponse.RoomOffer roomOffer = HotelResponse.RoomOffer.builder()
                            .roomType(offer.getRoom() != null ? offer.getRoom().getType() : "Standard")
                            .maxGuests(offer.getGuests() != null ? offer.getGuests().getAdults() : 2)
                            .pricePerNight(new BigDecimal(offer.getPrice().getBase()))
                            .totalPrice(new BigDecimal(offer.getPrice().getTotal()))
                            .currency(offer.getPrice().getCurrency())
                            .availableRooms(offer.getRoom() != null ? offer.getRoom().getQuantity() : 1)
                            .cancellationPolicy(offer.getPolicies() != null &&
                                    offer.getPolicies().getCancellation() != null ?
                                    offer.getPolicies().getCancellation().getDescription() : null)
                            .breakfastIncluded(false) // Check if breakfast is included
                            .refundable(offer.getPolicies() != null &&
                                    offer.getPolicies().getCancellation() != null ?
                                    offer.getPolicies().getCancellation().getRefundable() : false)
                            .build();

                    offers.add(roomOffer);
                }
            }
        }

        return offers;
    }

    private List<String> extractAmenities(List<AmadeusHotelResponse.HotelData.Amenity> amenities) {
        List<String> amenityList = new ArrayList<>();
        if (amenities != null) {
            for (AmadeusHotelResponse.HotelData.Amenity amenity : amenities) {
                if (amenity.getName() != null) {
                    amenityList.add(amenity.getName());
                }
            }
        }
        return amenityList;
    }

    private List<String> extractImages(List<AmadeusHotelResponse.HotelData.Media> media) {
        List<String> imageUrls = new ArrayList<>();
        if (media != null) {
            for (AmadeusHotelResponse.HotelData.Media mediaItem : media) {
                if (mediaItem.getUri() != null) {
                    imageUrls.add(mediaItem.getUri());
                }
            }
        }
        return imageUrls;
    }
}