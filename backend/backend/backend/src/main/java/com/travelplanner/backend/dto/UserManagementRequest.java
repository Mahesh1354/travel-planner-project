package com.travelplanner.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserManagementRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @Email(message = "Invalid email format")
    private String userEmail;

    @NotBlank(message = "Action is required")
    private String action; // DEACTIVATE, ACTIVATE, DELETE, CHANGE_ROLE

    private String newRole; // Only for CHANGE_ROLE

    private String reason;

    private boolean sendNotification = true;
}