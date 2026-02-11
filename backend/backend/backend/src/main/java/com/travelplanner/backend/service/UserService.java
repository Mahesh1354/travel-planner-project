package com.travelplanner.backend.service;

import com.travelplanner.backend.dto.AuthResponse;
import com.travelplanner.backend.dto.LoginRequest;
import com.travelplanner.backend.dto.RegisterRequest;
import com.travelplanner.backend.dto.UserResponse;

public interface UserService {

    UserResponse register(RegisterRequest registerRequest);

    AuthResponse authenticate(LoginRequest loginRequest);

    UserResponse getUserByEmail(String email);

    boolean emailExists(String email);
}
