package com.travelplanner.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthCheckDto {
    private String status; // UP, DOWN, DEGRADED
    private Map<String, String> details;
    private long timestamp;

    // Component health
    private ComponentHealth database;
    private ComponentHealth redis;
    private ComponentHealth externalApis;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentHealth {
        private String status;
        private String message;
        private Long responseTimeMs;
    }
}