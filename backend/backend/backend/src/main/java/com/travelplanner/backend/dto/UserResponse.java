package com.travelplanner.backend.dto;

import com.travelplanner.backend.entity.AuthProvider;
import com.travelplanner.backend.entity.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String profilePicture;
    private String phoneNumber;
    private UserType userType;
    private AuthProvider authProvider;
    private boolean isEmailVerified;
    private boolean isActive;
    private LocalDateTime createdAt;

    // Constructor from User entity
    public UserResponse(com.travelplanner.backend.entity.User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.profilePicture = user.getProfilePicture();
        this.phoneNumber = user.getPhoneNumber();
        this.userType = user.getUserType();
        this.authProvider = user.getAuthProvider();
        this.isEmailVerified = user.isEmailVerified();
        this.isActive = user.isActive();
        this.createdAt = user.getCreatedAt();
    }
}
