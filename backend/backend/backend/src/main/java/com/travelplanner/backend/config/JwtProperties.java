package com.travelplanner.backend.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    @Value("${jwt.secret-key}")
    private String secretKey = "${JWT_SECRET_KEY:}";
    private long expirationMs = 86400000; // 24 hours in milliseconds
    private String issuer = "travel-planner-app";
}