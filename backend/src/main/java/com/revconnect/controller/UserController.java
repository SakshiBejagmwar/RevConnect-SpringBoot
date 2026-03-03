package com.revconnect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.revconnect.service.UserService;
import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // Get all users
    @GetMapping
    public List<User> getAllUsers() {
        List<User> users = userService.getAllUsers();
        // Remove passwords from all users
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    // Get user by ID (with privacy enforcement)
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, Authentication authentication) {
        Long requestingUserId = getUserIdFromAuth(authentication);
        Optional<User> userOpt = userService.getUserById(id, requestingUserId);
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(userOpt.get());
    }

    // Get current user profile
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        Optional<User> userOpt = userService.getUserById(userId, userId);
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(userOpt.get());
    }

    // Update user profile
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @RequestBody User user,
                                        Authentication authentication) {
        try {
            Long requestingUserId = getUserIdFromAuth(authentication);
            User updatedUser = userService.updateUser(id, user, requestingUserId);
            
            if (updatedUser == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Change password
    @PostMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Long id,
                                           @RequestBody Map<String, String> passwordData,
                                           Authentication authentication) {
        try {
            Long requestingUserId = getUserIdFromAuth(authentication);
            
            if (!id.equals(requestingUserId)) {
                return ResponseEntity.status(403).body(Map.of("message", "Unauthorized"));
            }
            
            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");
            
            if (currentPassword == null || newPassword == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Current and new passwords are required"));
            }
            
            boolean success = userService.changePassword(id, currentPassword, newPassword);
            
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Failed to change password"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
        Long requestingUserId = getUserIdFromAuth(authentication);
        
        if (!id.equals(requestingUserId)) {
            return ResponseEntity.status(403).body(Map.of("message", "Unauthorized"));
        }
        
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    // Search users by name or username
    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String query) {
        List<User> users = userService.searchUsers(query);
        // Remove passwords
        users.forEach(user -> user.setPassword(null));
        return users;
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
