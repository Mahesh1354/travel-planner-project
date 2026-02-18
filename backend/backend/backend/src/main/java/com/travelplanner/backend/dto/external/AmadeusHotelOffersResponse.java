package com.travelplanner.backend.dto.external;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class AmadeusHotelOffersResponse {

    private List<HotelOffer> data;

    @Data
    public static class HotelOffer {
        private String type;
        private Hotel hotel;
        private String hotelId;
        private String roomType;
        private List<Offer> offers;

        @Data
        public static class Hotel {
            private String type;
            private String hotelId;
            private String name;
            private String chainCode;
            private Double latitude;
            private Double longitude;
            private String cityCode;
            private Address address;

            @Data
            public static class Address {
                private String lines;
                private String postalCode;
                private String cityName;
                private String countryCode;
            }
        }

        @Data
        public static class Offer {
            private String id;
            private String checkInDate;
            private String checkOutDate;
            private String rateCode;
            private Category category;
            private Description description;
            private Room room;
            private Guests guests;
            private Price price;
            private Policies policies;
            private String self;

            @Data
            public static class Category {
                private String name;
                private String code;
            }

            @Data
            public static class Description {
                private String lang;
                private String text;
            }

            @Data
            public static class Room {
                private String type;
                private String typeEstimated;
                private Description description;
                private Integer quantity;
            }

            @Data
            public static class Guests {
                private Integer adults;
                private Integer children;
            }

            @Data
            public static class Price {
                private String currency;
                private String total;
                private String base;
                private List<Tax> taxes;
                private Variations variations;

                @Data
                public static class Tax {
                    private String code;
                    private String amount;
                    private String currency;
                    private Boolean included;
                }

                @Data
                public static class Variations {
                    private List<Change> changes;

                    @Data
                    public static class Change {
                        private String startDate;
                        private String endDate;
                        private String total;
                        private String base;
                    }
                }
            }

            @Data
            public static class Policies {
                private String holdTime;
                private Cancellation cancellation;
                private PaymentType paymentType;

                @Data
                public static class Cancellation {
                    private String type;
                    private String description;
                    private Deadline deadline;
                    private Double amount;
                    private String currency;
                    private Boolean refundable;

                    @Data
                    public static class Deadline {
                        private String date;
                        private String time;
                    }
                }

                @Data
                public static class PaymentType {
                    private List<String> methods;
                    private String creditCard;
                    private String deposit;
                }
            }
        }
    }

    @Data
    public static class Dictionaries {
        private Amenities amenities;
        private RoomTypes roomTypes;

        @Data
        public static class Amenities {
            // Dynamic mapping
        }

        @Data
        public static class RoomTypes {
            // Dynamic mapping
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
        }
    }
}