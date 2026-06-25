package com.travelplanner.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogoutResponse {
    private boolean success;
    private String message;
    private String timestamp;

    public LogoutResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }
}
