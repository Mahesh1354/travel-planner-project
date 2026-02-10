package com.travelplanner.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryExportResponse {
    private String downloadUrl;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String exportId;
    private LocalDateTime exportedAt;
    private boolean requiresPassword;
}
