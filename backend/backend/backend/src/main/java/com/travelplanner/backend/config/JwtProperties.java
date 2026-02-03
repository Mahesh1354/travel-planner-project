package com.travelplanner.backend.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secretKey = "your-secret-key-for-travel-planner-application-2026-encryption";
    private long expirationMs = 86400000; // 24 hours in milliseconds
    private String issuer = "travel-planner-app";
}