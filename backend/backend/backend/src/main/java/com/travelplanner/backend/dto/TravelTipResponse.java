package com.travelplanner.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelTipResponse {
    private Long id;
    private String tipType;
    private String title;
    private String content;
    private String country;
    private String city;
    private Integer priorityLevel;
    private boolean isAlert;
    private LocalDateTime alertExpiry;
    private String source;
    private String sourceUrl;
    private LocalDateTime createdAt;

    // Constructor from TravelTip entity
    public TravelTipResponse(com.travelplanner.backend.entity.TravelTip tip) {
        this.id = tip.getId();
        this.tipType = tip.getTipType();
        this.title = tip.getTitle();
        this.content = tip.getContent();
        this.country = tip.getCountry();
        this.city = tip.getCity();
        this.priorityLevel = tip.getPriorityLevel();
        this.isAlert = tip.isAlert();
        this.alertExpiry = tip.getAlertExpiry();
        this.source = tip.getSource();
        this.sourceUrl = tip.getSourceUrl();
        this.createdAt = tip.getCreatedAt();
    }
}