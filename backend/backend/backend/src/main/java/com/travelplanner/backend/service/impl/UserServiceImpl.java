package com.travelplanner.backend.service.impl;

import com.travelplanner.backend.dto.AuthResponse;
import com.travelplanner.backend.dto.LoginRequest;
import com.travelplanner.backend.dto.RegisterRequest;
import com.travelplanner.backend.dto.UserResponse;
import com.travelplanner.backend.entity.User;
import com.travelplanner.backend.entity.UserType;
import com.travelplanner.backend.exception.AuthenticationException;
import com.travelplanner.backend.exception.ResourceNotFoundException;
import com.travelplanner.backend.exception.UnauthorizedAccessException;
import com.travelplanner.backend.exception.UserAlreadyExistsException;
import com.travelplanner.backend.repository.UserRepository;
import com.travelplanner.backend.service.JwtService;
import com.travelplanner.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    // Password regex: minimum 8 characters, at least one letter and one number
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        logger.info("Attempting to register user with email: {}", registerRequest.getEmail());

        // Validate email format
        if (!isValidEmail(registerRequest.getEmail())) {
            logger.warn("Invalid email format: {}", registerRequest.getEmail());
            throw new IllegalArgumentException("Invalid email format");
        }

        // Validate password strength
        if (!isValidPassword(registerRequest.getPassword())) {
            logger.warn("Password does not meet strength requirements");
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters and contain both letters and numbers"
            );
        }

        // Normalize email to lowercase for case-insensitive uniqueness
        String normalizedEmail = registerRequest.getEmail().toLowerCase().trim();

        // Check if email already exists
        if (emailExists(normalizedEmail)) {
            logger.warn("Registration failed - email already exists: {}", normalizedEmail);
            throw new UserAlreadyExistsException("Email already registered");
        }

        User user = new User();
        user.setFullName(registerRequest.getFullName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPhoneNumber(registerRequest.getPhoneNumber() != null ?
                registerRequest.getPhoneNumber().trim() : null);
        user.setProfilePicture(registerRequest.getProfilePicture());
        user.setUserType(UserType.INDIVIDUAL_TRAVELER); // Default user type
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        logger.info("User registered successfully with ID: {}, email: {}",
                savedUser.getId(), savedUser.getEmail());

        return new UserResponse(savedUser);
    }

    @Override
    @Transactional
    public AuthResponse authenticate(LoginRequest loginRequest) {
        logger.info("Login attempt for email: {}", loginRequest.getEmail());

        // Normalize email to lowercase
        String normalizedEmail = loginRequest.getEmail().toLowerCase().trim();

        // Find user by email
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> {
                    logger.warn("Login failed - user not found: {}", normalizedEmail);
                    return new AuthenticationException("Invalid credentials");
                });

        if (!user.isActive()) {
            logger.warn("Login failed - account deactivated for user: {}", normalizedEmail);
            throw new AuthenticationException("Account is deactivated. Please contact support.");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            logger.warn("Login failed - invalid password for user: {}", normalizedEmail);
            throw new AuthenticationException("Invalid credentials");
        }

        // Update last login time
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        logger.info("User logged in successfully: {}", normalizedEmail);

        // Generate JWT token
        String token = jwtService.generateToken(user);

        return new AuthResponse(token, new UserResponse(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        logger.debug("Fetching user by email: {}", email);

        String normalizedEmail = email.toLowerCase().trim();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", normalizedEmail);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });

        return new UserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        return userRepository.existsByEmail(normalizedEmail);
    }

    @Override
    @Transactional
    public void upgradeToGroupTraveler(Long userId, Authentication authentication) {
        logger.info("Attempting to upgrade user ID: {} to GROUP_TRAVELER", userId);

        // Email comes from JWT subject
        String email = authentication.getName();
        String normalizedEmail = email.toLowerCase().trim();

        User loggedInUser = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> {
                    logger.error("Authenticated user not found: {}", normalizedEmail);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });

        // Verify user is upgrading their own account
        if (!loggedInUser.getId().equals(userId)) {
            logger.warn("User {} attempted to upgrade account of user {}",
                    loggedInUser.getId(), userId);
            throw new UnauthorizedAccessException("You can upgrade only your own account");
        }

        // Check if user is eligible for upgrade
        if (loggedInUser.getUserType() == UserType.GROUP_TRAVELER) {
            logger.warn("User {} is already a GROUP_TRAVELER", loggedInUser.getId());
            throw new IllegalStateException("User is already a group traveler");
        }

        if (loggedInUser.getUserType() != UserType.INDIVIDUAL_TRAVELER) {
            logger.warn("User {} with type {} cannot be upgraded to GROUP_TRAVELER",
                    loggedInUser.getId(), loggedInUser.getUserType());
            throw new IllegalStateException("Only individual travelers can be upgraded to group travelers");
        }

        // Perform upgrade
        loggedInUser.setUserType(UserType.GROUP_TRAVELER);
        loggedInUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(loggedInUser);

        logger.info("User {} successfully upgraded to GROUP_TRAVELER", loggedInUser.getId());
    }

    // ==================== HELPER METHODS ====================

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email.trim()).matches();
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}