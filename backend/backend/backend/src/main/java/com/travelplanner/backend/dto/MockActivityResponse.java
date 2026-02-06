package com.travelplanner.backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MockActivityResponse {
    private boolean success;
    private String message;
    private List<MockActivity> activities;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MockActivity {
        private String id;
        private String name;
        private String type;
        private String location;
        private String description;
        private Double price;
        private String currency;
        private String duration;
        private String difficultyLevel;
        private Integer minAge;
        private Integer maxParticipants;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String meetingPoint;
        private String included;
        private String notIncluded;
        private String guideLanguage;
        private Double rating;
        private Integer reviewCount;
        private Boolean instantConfirmation;
    }
}
