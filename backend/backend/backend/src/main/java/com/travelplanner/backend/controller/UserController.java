package com.travelplanner.backend.controller;

import com.travelplanner.backend.dto.ApiResponse;
import com.travelplanner.backend.dto.AuthResponse;
import com.travelplanner.backend.dto.LoginRequest;
import com.travelplanner.backend.dto.RegisterRequest;
import com.travelplanner.backend.dto.UserResponse;
import com.travelplanner.backend.service.JwtService;
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

    @Autowired
    private JwtService jwtService;

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
}
