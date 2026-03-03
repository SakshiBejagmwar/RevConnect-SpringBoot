package com.revconnect.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;
import com.revconnect.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account with email, username, and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or user already exists",
            content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // Validate required fields
            if (user.getName() == null || user.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Name is required"));
            }
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Username is required"));
            }
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
            }
            if (user.getPassword() == null || user.getPassword().length() < 6) {
                return ResponseEntity.badRequest().body(Map.of("message", "Password must be at least 6 characters"));
            }

            // Check if email already exists
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email already registered"));
            }

            // Check if username already exists
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Username already taken"));
            }

            // Encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Set default role if not provided
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("USER");
            }

            // Set default account privacy if not provided
            if (user.getAccountPrivacy() == null || user.getAccountPrivacy().isEmpty()) {
                user.setAccountPrivacy("PUBLIC");
            }

            // Save user
            User savedUser = userRepository.save(user);

            // Generate token
            String token = jwtUtil.generateToken(savedUser.getEmail());

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            
            // Remove password from user object before sending
            savedUser.setPassword(null);
            response.put("user", savedUser);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with email/username and password, returns JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "400", description = "Invalid credentials",
            content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String emailOrUsername = loginRequest.get("emailOrUsername");
            String password = loginRequest.get("password");

            if (emailOrUsername == null || emailOrUsername.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email or username is required"));
            }
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Password is required"));
            }

            // Find user by email or username
            Optional<User> userOpt = userRepository.findByEmailOrUsername(emailOrUsername, emailOrUsername);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "User not found"));
            }

            User existingUser = userOpt.get();

            // Verify password
            if (!passwordEncoder.matches(password, existingUser.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid password"));
            }

            // Generate token
            String token = jwtUtil.generateToken(existingUser.getEmail());

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            
            // Remove password from user object before sending
            existingUser.setPassword(null);
            response.put("user", existingUser);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Login failed: " + e.getMessage()));
        }
    }
}


