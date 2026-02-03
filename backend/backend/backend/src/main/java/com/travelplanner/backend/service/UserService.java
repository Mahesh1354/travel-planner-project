package com.travelplanner.backend.service;

import com.travelplanner.backend.dto.AuthResponse;
import com.travelplanner.backend.dto.RegisterRequest;
import com.travelplanner.backend.dto.LoginRequest;
import com.travelplanner.backend.dto.UserResponse;

public interface UserService {

    // Register a new user
    UserResponse register(RegisterRequest registerRequest);

    // Authenticate user (login)
    AuthResponse authenticate(LoginRequest loginRequest);

    // Get user by email
    UserResponse getUserByEmail(String email);

    // Check if email exists
    boolean emailExists(String email);
}