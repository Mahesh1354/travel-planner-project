package com.travelplanner.backend.service.impl;

import com.travelplanner.backend.dto.AuthResponse;
import com.travelplanner.backend.dto.LoginRequest;
import com.travelplanner.backend.dto.RegisterRequest;
import com.travelplanner.backend.dto.UserResponse;
import com.travelplanner.backend.entity.User;
import com.travelplanner.backend.entity.UserType;
import com.travelplanner.backend.exception.AuthenticationException;
import com.travelplanner.backend.exception.UserAlreadyExistsException;
import com.travelplanner.backend.repository.UserRepository;
import com.travelplanner.backend.service.JwtService;
import com.travelplanner.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Override
    public UserResponse register(RegisterRequest registerRequest) {
        // Check if email already exists
        if (emailExists(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        // Create new user
        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setProfilePicture(registerRequest.getProfilePicture());
        user.setUserType(UserType.INDIVIDUAL_TRAVELER);

        // Save user to database
        User savedUser = userRepository.save(user);

        // Convert to response DTO
        return new UserResponse(savedUser);
    }

    @Override
    public AuthResponse authenticate(LoginRequest loginRequest) {
        // Find user by email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AuthenticationException("User not found"));

        // Check if user is active
        if (!user.isActive()) {
            throw new AuthenticationException("Account is deactivated");
        }

        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }

        // Generate JWT token
        String token = jwtService.generateToken(user);

        // Convert user to response DTO
        UserResponse userResponse = new UserResponse(user);

        // Return auth response with token
        return new AuthResponse(token, userResponse);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("User not found"));
        return new UserResponse(user);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}