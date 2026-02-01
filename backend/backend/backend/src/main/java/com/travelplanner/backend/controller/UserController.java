package com.travelplanner.backend.controller;
import com.travelplanner.dto.ApiResponse;
import com.travelplanner.backend.dto.AuthResponse;
import com.travelplanner.backend.dto.LoginRequest;
import com.travelplanner.backend.dto.RegisterRequest;
import com.travelplanner.backend.dto.UserResponse;
import com.travelplanner.backend.entity.User;
import com.travelplanner.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            UserResponse userResponse = userService.register(registerRequest);

            // For now, return user without token (JWT will be added later)
            ApiResponse response = ApiResponse.success(
                    "Registration successful. Please login.",
                    userResponse
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.authenticate(loginRequest);

            // Convert user to response DTO
            UserResponse userResponse = new UserResponse(user);

            // Create auth response (without JWT token for now)
            AuthResponse authResponse = new AuthResponse(
                    "token_will_be_implemented_later", // Placeholder
                    userResponse
            );

            ApiResponse response = ApiResponse.success(
                    "Login successful",
                    authResponse
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<ApiResponse> checkEmailExists(@PathVariable String email) {
        boolean exists = userService.emailExists(email);

        ApiResponse response;
        if (exists) {
            response = ApiResponse.success("Email already registered", true);
        } else {
            response = ApiResponse.success("Email is available", false);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/{email}")
    public ResponseEntity<ApiResponse> getUserProfile(@PathVariable String email) {
        try {
            UserResponse userResponse = userService.getUserByEmail(email);

            ApiResponse response = ApiResponse.success(
                    "User profile retrieved successfully",
                    userResponse
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("Failed to retrieve user profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}