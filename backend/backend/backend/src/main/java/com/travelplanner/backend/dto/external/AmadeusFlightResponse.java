package com.travelplanner.backend.dto.external;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

@Data
public class AmadeusFlightResponse {

    private List<FlightOffer> data;
    private Dictionaries dictionaries;
    private Meta meta;

    @Data
    public static class FlightOffer {
        private String type;
        private String id;
        private String source;
        private boolean instantTicketingRequired;
        private boolean nonHomogeneous;
        private boolean paymentCardRequired;
        private List<Itinerary> itineraries;
        private Price price;
        private PricingOptions pricingOptions;
        private List<String> validatingAirlineCodes;
        private List<Segment> segments;

        @Data
        public static class Itinerary {
            private String duration;
            private List<Segment> segments;
        }

        @Data
        public static class Segment {
            private Departure departure;
            private Arrival arrival;
            private String carrierCode;
            private String number;
            private Aircraft aircraft;
            private String duration;
            private String id;
            private int numberOfStops;
            private boolean blacklistedInEU;

            @Data
            public static class Departure {
                private String iataCode;
                private String terminal;
                private String at;
            }

            @Data
            public static class Arrival {
                private String iataCode;
                private String terminal;
                private String at;
            }

            @Data
            public static class Aircraft {
                private String code;
            }
        }

        @Data
        public static class Price {
            private String currency;
            private String total;
            private String base;
            private List<Fee> fees;
            private String grandTotal;

            @Data
            public static class Fee {
                private String amount;
                private String type;
            }
        }

        @Data
        public static class PricingOptions {
            private List<String> fareType;
            private boolean includedCheckedBagsOnly;
        }
    }

    @Data
    public static class Dictionaries {
        private Locations locations;
        private Aircraft aircraft;
        private Currencies currencies;
        private Carriers carriers;

        @Data
        public static class Locations {
            // Dynamic mapping for location codes
        }

        @Data
        public static class Aircraft {
            // Dynamic mapping for aircraft codes
        }

        @Data
        public static class Currencies {
            // Dynamic mapping for currency codes
        }

        @Data
        public static class Carriers {
            // Dynamic mapping for airline codes
        }
    }

    @Data
    public static class Meta {
        private int count;
        private Links links;

        @Data
        public static class Links {
            private String self;
            private String next;
            private String last;
        }
    }
}