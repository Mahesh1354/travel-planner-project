package com.travelplanner.backend.dto.external;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;


@Data
public class AmadeusHotelResponse {

    private List<HotelData> data;

    @Data
    public static class HotelData {
        private String type;
        private String hotelId;
        private String name;
        private String chainCode;
        private String iataCode;
        private Double distance;
        private String distanceUnit;
        private Address address;
        private Contact contact;
        private Description description;
        private GeoCode geoCode;
        private List<Amenity> amenities;
        private List<Media> media;
        private Integer starRating;
        private Double rating;
        private Integer reviewCount;
        private Offers offers;

        @Data
        public static class GeoCode {
            private Double latitude;
            private Double longitude;
        }

        @Data
        public static class Address {
            private String lines;
            private String postalCode;
            private String cityName;
            private String countryCode;
            private String stateCode;
        }

        @Data
        public static class Contact {
            private String phone;
            private String fax;
            private String email;
        }

        @Data
        public static class Description {
            private String lang;
            private String text;
        }

        @Data
        public static class Amenity {
            private String code;
            private String name;
            private String description;
        }

        @Data
        public static class Media {
            private String uri;
            private String category;
            private String description;
        }

        @Data
        public static class Offers {
            private List<Offer> offers;

            @Data
            public static class Offer {
                private String id;
                private String checkInDate;
                private String checkOutDate;
                private String roomType;
                private String bedType;
                private Integer guests;
                private Price price;
                private CancellationPolicy cancellationPolicy;
                private Boolean breakfastIncluded;
                private Integer availableRooms;

                @Data
                public static class Price {
                    private String currency;
                    private String total;
                    private String base;
                    private List<Tax> taxes;

                    @Data
                    public static class Tax {
                        private String code;
                        private String amount;
                        private String currency;
                        private String type;
                    }
                }

                @Data
                public static class CancellationPolicy {
                    private String description;
                    private String type;
                    private String deadline;
                    private Double penalty;
                    private String penaltyCurrency;
                }
            }
        }
    }

    @Data
    public static class Meta {
        private Integer count;
        private Links links;

        @Data
        public static class Links {
            private String self;
            private String next;
            private String last;
        }
    }
}