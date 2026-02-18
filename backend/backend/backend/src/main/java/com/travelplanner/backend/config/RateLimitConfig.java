package com.travelplanner.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rate.limit")
public class RateLimitConfig {
    private int auth = 5;
    private int authWindowMinutes = 1;
    private int admin = 100;
    private int adminWindowMinutes = 1;
    private int defaultLimit = 60;
    private int defaultWindowMinutes = 1;
}