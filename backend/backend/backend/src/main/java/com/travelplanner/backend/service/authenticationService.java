package com.travelplanner.backend.service;

import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.entity.User;
import com.travelplanner.backend.entity.UserType;
import com.travelplanner.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class authenticationService {

    private final UserRepository userRepository = null;
    private final PasswordEncoder passwordEncoder = null;

    // ================= REGISTER =================
    public ApiResponse register(RegisterRequest request) {

        Map<String, String> errors = new HashMap<>();

        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            errors.put("fullName", "Full name is required");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            errors.put("email", "Email is required");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            errors.put("password", "Password must be at least 6 characters");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            errors.put("email", "Email already registered");
        }

        if (!errors.isEmpty()) {
            return ApiResponse.<UserResponse>builder()
                    .success(false)
                    .message("Validation failed")
                    .errors(errors)
                    .build();
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setProfilePicture(request.getProfilePicture());
        user.setUserType(UserType.INDIVIDUAL_TRAVELER);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        UserResponse response = UserResponse.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .build();

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User registered successfully")
                .data(response)
                .build();
    }

    // ================= LOGIN =================
    @SuppressWarnings("unused")
	public ApiResponse<UserResponse> login(LoginRequest request) {

        Map<String, String> errors = new HashMap<>();

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            errors.put("email", "Email is required");
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            errors.put("password", "Password is required");
        }

        if (!errors.isEmpty()) {
            return ApiResponse.<UserResponse>builder()
                    .success(false)
                    .message("Validation failed")
                    .errors(errors)
                    .build();
        }

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return ApiResponse.<UserResponse>builder()
                    .success(false)
                    .message("Invalid email or password")
                    .build();
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ApiResponse.<UserResponse>builder()
                    .success(false)
                    .message("Invalid email or password")
                    .build();
        }

        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Login successful")
                .data(response)
                .build();
    }
}
