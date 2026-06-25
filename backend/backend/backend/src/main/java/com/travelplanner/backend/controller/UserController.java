package com.travelplanner.backend.controller;

import com.travelplanner.backend.dto.*;
import com.travelplanner.backend.service.JwtService;
import com.travelplanner.backend.service.UserService;
import com.travelplanner.backend.service.TokenBlacklistService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            UserResponse userResponse = userService.register(registerRequest);

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
            AuthResponse authResponse = userService.authenticate(loginRequest);

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

        if (exists) {
            return ResponseEntity.ok(
                    ApiResponse.success("Email already registered", true)
            );
        }

        return ResponseEntity.ok(
                ApiResponse.success("Email is available", false)
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getCurrentUserProfile(
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.substring(7);
        String email = jwtService.getEmailFromToken(token);

        UserResponse userResponse = userService.getUserByEmail(email);

        return ResponseEntity.ok(
                ApiResponse.success("User profile retrieved successfully", userResponse)
        );
    }

    @PutMapping("/{id}/upgrade-to-group")
    public ResponseEntity<?> upgradeToGroup(
            @PathVariable Long id,
            Authentication authentication) {

        userService.upgradeToGroupTraveler(id, authentication);

        return ResponseEntity.ok("User upgraded to GROUP_TRAVELER");
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logoutUser(
            HttpServletRequest request,
            HttpServletResponse response) {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LogoutResponse(false, "No valid token found"));
        }

        String token = authHeader.substring(7);

        try {
            if (jwtService.validateToken(token)) {
                // Get expiration from JwtService, then pass to TokenBlacklistService
                LocalDateTime expiresAt = jwtService.getClaimsFromToken(token)
                        .getExpiration()
                        .toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime();

                tokenBlacklistService.blacklistToken(token, "USER_LOGOUT", expiresAt);

                // Clear client-side token header
                response.setHeader("Authorization", "");

                return ResponseEntity.ok()
                        .body(new LogoutResponse(true, "Successfully logged out"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LogoutResponse(false, "Invalid token"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LogoutResponse(false, "Logout failed: " + e.getMessage()));
        }
    }
}