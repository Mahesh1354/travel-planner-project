package com.travelplanner.backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceResponse {
    private Long id;
    private String preferenceType;
    private String preferenceKey;
    private String preferenceValue;
    private Double weight;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponse user;

    // Constructor from UserPreference entity
    public UserPreferenceResponse(com.travelplanner.backend.entity.UserPreference preference) {
        this.id = preference.getId();
        this.preferenceType = preference.getPreferenceType();
        this.preferenceKey = preference.getPreferenceKey();
        this.preferenceValue = preference.getPreferenceValue();
        this.weight = preference.getWeight();
        this.createdAt = preference.getCreatedAt();
        this.updatedAt = preference.getUpdatedAt();
        this.user = new UserResponse(preference.getUser());
    }
}
