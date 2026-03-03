package com.revconnect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.revconnect.service.NotificationService;
import com.revconnect.entity.Notification;
import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    // Get my notifications
    @GetMapping
    public ResponseEntity<List<Notification>> getMyNotifications(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // Get unread notification count
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        Long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // Mark notification as read
    @PutMapping("/read/{id}")
    public ResponseEntity<?> markAsRead(@PathVariable Long id,
                                        Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            notificationService.markAsRead(id, userId);
            return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not authorized")) {
                return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Mark all notifications as read
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }

    // Get notification preferences
    @GetMapping("/preferences")
    public ResponseEntity<?> getPreferences(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return ResponseEntity.ok(notificationService.getPreferences(userId));
    }

    // Update notification preferences
    @PutMapping("/preferences")
    public ResponseEntity<?> updatePreferences(@RequestBody com.revconnect.entity.NotificationPreference preferences,
                                               Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            com.revconnect.entity.NotificationPreference updated = notificationService.updatePreferences(userId, preferences);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
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
