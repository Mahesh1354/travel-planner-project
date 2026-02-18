package com.travelplanner.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkOperationResponse {
    private String operation;
    private int totalProcessed;
    private int successCount;
    private int failureCount;
    private List<Long> successIds;
    private Map<Long, String> failures; // userId -> error message
    private String message;
}