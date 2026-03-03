package com.revconnect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.revconnect.entity.BusinessProfile;
import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.BusinessProfileService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/business")
public class BusinessProfileController {

    @Autowired
    private BusinessProfileService service;

    @Autowired
    private UserRepository userRepository;

    // Create or update business profile (authenticated)
    @PostMapping("/profile")
    public ResponseEntity<?> createOrUpdateProfile(@RequestBody BusinessProfile profile,
                                                    Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            profile.setUserId(userId);
            BusinessProfile saved = service.createOrUpdateProfile(profile);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Get my business profile
    @GetMapping("/profile")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        Optional<BusinessProfile> profile = service.getByUserId(userId);
        return profile.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // Get business profile by user ID (public)
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        Optional<BusinessProfile> profile = service.getByUserId(userId);
        return profile.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // Helper method to extract user ID from authentication
    private Long getUserIdFromAuth(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        String email = authentication.getPrincipal().toString();
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        return userOpt.get().getId();
    }
}
